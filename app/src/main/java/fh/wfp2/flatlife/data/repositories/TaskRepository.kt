package fh.wfp2.flatlife.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.remote.TaskApi
import fh.wfp2.flatlife.data.room.daos.TaskDao
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.other.networkBoundResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApi: TaskApi,
    private val context: Application
) :
    AbstractRepository<Task>(taskDao) {
    private val todoRepositoryJob = Job()
    private val ioScope = CoroutineScope(todoRepositoryJob + Dispatchers.IO)
    private var curTasksResponse: Response<List<Task>>? = null

    suspend fun insertTask(task: Task) {
        val response = try {
            taskApi.addTask(task)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            taskDao.insert(task)
        } else {
            taskDao.insert(task)
        }
    }

    private suspend fun insertTasks(tasks: List<Task>) {
        tasks.forEach { insertTask(it) }
    }

    suspend fun deleteTask(task: Task) {
        val response = try {
            taskApi.deleteTask(task)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            taskDao.delete(task)
        } else {
            //todo in db markieren als gelöscht
        }
    }

    private suspend fun syncTasks() {
     /*   val locallyDeletedTaskIDs = taskDao.getAllLocallyDeletedTasksIDs()
        locallyDeletedTaskIDs.forEach { id -> deleteTask(id.deletedTaskID) }
*/

        val unsyncedTasks: List<Task> = taskDao.getAllUnsyncedTasks()

        unsyncedTasks.forEach { task -> insertTask(task) }
        curTasksResponse = taskApi.getAllTasks()
        curTasksResponse?.body()?.let { tasks ->
            taskDao.deleteAllTasks()
            tasks.forEach { task ->
                taskDao.insert(task.apply { isSynced = true })
            }
        }
    }

    fun getAllTasks(): Flow<Resource<List<Task>>> {
        return networkBoundResource(
            query = {
                taskDao.getAllTasks()
            },
            fetch = {
                syncTasks()
                curTasksResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertTasks(it.onEach { task -> task.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

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