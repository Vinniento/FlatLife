package fh.wfp2.flatlife

import android.app.Application
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.room.TaskRoomDatabase
import timber.log.Timber

class ApplicationFlatLife : Application() {

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy {
        TaskRoomDatabase.getInstance(this)
    }

    val repository: TaskRepository by lazy { TaskRepository(database.taskDao()) }


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i("onCreate called")
    }


}