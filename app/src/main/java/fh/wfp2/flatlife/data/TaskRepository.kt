package fh.wfp2.flatlife.data

import androidx.lifecycle.LiveData
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao

class TaskRepository(private val taskDao: TaskDao) {
    //object TaskRepository{
    //  private val taskDao: TaskDao

    //val allTasks: Flow<List<Task>> = taskDao.getAllTasks() --> original so aber dann muss man im TasksViewModel .asLiveData() machen, was nicht geht
    //val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    fun getHighestTask(): Task? = taskDao.getHighestID()

    /*   companion object {

           // For Singleton instantiation
           @Volatile private var instance: TaskRepository? = null

           fun getInstance(taskDao: TaskDao) =
               instance ?: synchronized(this) {
                   instance ?: TaskRepository(taskDao).also { instance = it }
               }
       }*/
}

