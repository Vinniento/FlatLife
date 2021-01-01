package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber


class TaskRepository(private val taskDao: TaskDao) : AbstractRepository<Task>(taskDao) {
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
}

