package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fh.wfp2.flatlife.data.room.daos.*
import fh.wfp2.flatlife.data.room.entities.*
import java.util.concurrent.Executors

@Database(
    entities = [Task::class, ShoppingItem::class, ExpenseCategory::class, FinanceActivity::class, Chore::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlatLifeRoomDatabase : RoomDatabase() {

    abstract fun shoppingDao(): ShoppingDao
    abstract fun taskDao(): TaskDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun addExpenseDao(): AddExpenseDao
    abstract fun financeActivityDao(): FinanceActivityDao
    abstract fun choresDao(): ChoresDao

    companion object {
        @Volatile
        private var INSTANCE: FlatLifeRoomDatabase? = null

        private val PREPOPULATE_CATEGORIES =
            listOf<ExpenseCategory>(
                ExpenseCategory(0, "Groceries"),
                ExpenseCategory(0, "Household"),
                ExpenseCategory(0, "Car"),
                ExpenseCategory(0, "Garden"),
            )

        fun getInstance(
            context: Context
        )
                : FlatLifeRoomDatabase {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlatLifeRoomDatabase::class.java,
                    "database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            ioThread {
                                getInstance(context).expenseCategoryDao().insertList(
                                    PREPOPULATE_CATEGORIES
                                )
                            }

                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}