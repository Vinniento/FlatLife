package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fh.wfp2.flatlife.data.room.daos.ExpenseCategoryDao
import fh.wfp2.flatlife.data.room.daos.FinanceDao
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.entities.ShoppingItem

@Database(entities = [Task::class, ShoppingItem::class, ExpenseCategory::class, FinanceActivity::class], version = 1, exportSchema = false)
abstract class FlatLifeRoomDatabase : RoomDatabase() {

    abstract fun shoppingDao(): ShoppingDao
    abstract fun taskDao(): TaskDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun financeDao(): FinanceDao

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
                    "database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}