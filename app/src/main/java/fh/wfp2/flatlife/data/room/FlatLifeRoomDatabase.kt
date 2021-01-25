package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fh.wfp2.flatlife.data.room.daos.*
import fh.wfp2.flatlife.data.room.entities.*

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
                    "flatlife_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
