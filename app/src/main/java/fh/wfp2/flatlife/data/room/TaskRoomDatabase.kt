package fh.wfp2.flatlife.data.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao //fürs testen

    /*private class TaskDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { taskRoomDatabase ->
                scope.launch {
                    val taskDao = taskRoomDatabase.taskDao()

                    val task = Task(
                        id = 4,
                        name = "Küche",
                        dueBy = "23.12.2020",
                        createdAt = "11.12.2020",
                        createdBy = "Vince"
                    )
                    taskDao.insert(task)

                }
            }
        }
    }*/


    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getInstance(
            context: Context
        )
                : TaskRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration() //wenn DB schema sich ändert einfach löschen und neubauen -> daten weg
                    //.addCallback(TaskDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}