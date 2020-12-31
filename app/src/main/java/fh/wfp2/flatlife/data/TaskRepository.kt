package fh.wfp2.flatlife.data

import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.data.room.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber


class TaskRepository(private val taskDao: TaskDao) {
    val todoRepositoryJob = Job()

    private val ioScope = CoroutineScope(todoRepositoryJob + Dispatchers.IO)

    suspend fun insert(task: Task) {
        ioScope.launch {
            taskDao.insert(task)
        }
    }

    fun getTasks(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Task>> {
        Timber.i("getTodos called")

        return taskDao.getTasks(searchQuery, hideCompleted, sortOrder)
    }

    suspend fun update(task: Task) {
        ioScope.launch {
            taskDao.update(
                task
            )
        }
    }

    suspend fun deleteTask(task: Task) {
        ioScope.launch {

            taskDao.delete(task)
        }
    }

    suspend fun deleteAllCompletedTasks() {
        ioScope.launch {
            taskDao.deleteAllCompletedTasks()
        }
    }
}

