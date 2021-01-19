package fh.wfp2.flatlife.data.repositories

import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.daos.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class TaskRepository @Inject constructor(private val taskDao: TaskDao) :
    AbstractRepository<Task>(taskDao) {
    val todoRepositoryJob = Job()

    private val ioScope = CoroutineScope(todoRepositoryJob + Dispatchers.IO)

    fun getTasks(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Task>> {
        Timber.i("getTodos called")

        return taskDao.getTasks(searchQuery, hideCompleted, sortOrder)
    }

    suspend fun deleteAllCompletedTasks() {
        ioScope.launch {
            taskDao.deleteAllCompletedTasks()
        }
    }

    fun getAllItems(): Flow<List<Task>> = taskDao.getAllTasks()

}

