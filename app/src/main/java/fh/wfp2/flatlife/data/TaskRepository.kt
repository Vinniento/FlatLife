package fh.wfp2.flatlife.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao

class TaskRepository(private val taskDao: TaskDao) {

    //val allTasks: Flow<List<Task>> = taskDao.getAllTasks() --> original so aber dann muss man im TasksViewModel .asLiveData() machen, was nicht geht
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }
}