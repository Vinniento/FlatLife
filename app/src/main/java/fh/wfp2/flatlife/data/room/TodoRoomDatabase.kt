package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoRoomDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao //fürs testen


    companion object {
        @Volatile
        private var INSTANCE: TodoRoomDatabase? = null

        fun getInstance(
            context: Context
        )
                : TodoRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoRoomDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}