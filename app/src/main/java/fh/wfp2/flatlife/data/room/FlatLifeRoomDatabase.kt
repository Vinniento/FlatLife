package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.data.room.entities.Task

@Database(entities = [Task::class, ShoppingItem::class], version = 1, exportSchema = false)
abstract class FlatLifeRoomDatabase : RoomDatabase() {

    abstract fun shoppingDao(): ShoppingDao
    abstract fun taskDao(): TaskDao


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