package fh.wfp2.flatlife.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.data.remote.requests.DeleteTaskRequest
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
) : AbstractRepository<Task>(taskDao) {

    private val todoRepositoryJob = Job()
    private val ioScope = CoroutineScope(todoRepositoryJob + Dispatchers.IO)

    suspend fun insertTask(task: Task) {

        val response = try {
            taskApi.addTask(task)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            taskDao.insert(task.apply { isSynced = true; id = response.body()?.taskId!! })
        } else {
            taskDao.insert(task)
        }
    }


    private suspend fun insertTasks(tasks: List<Task>) {
        tasks.forEach { insertTask(it) }
    }

    suspend fun deleteTask(task: Task): Boolean {

        val response = try {
            taskApi.deleteTask(DeleteTaskRequest(task.id))
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            taskDao.delete(task)
            true;
        } else {
            taskDao.insert(task.apply { isDeletedLocally = true })
            true;
        }

    }

    private var curTaskResponse: Response<List<Task>>? = null

    private suspend fun syncTasks() {
        val locallyDeleteTaskIDs = taskDao.getLocallyDeletedTaskIDs()
        locallyDeleteTaskIDs.forEach { task -> deleteTask(task) }

        val unsyncedTasks = taskDao.getAllUnsyncedTasks()
        unsyncedTasks.forEach {
            insertTask(it)
        }
        curTaskResponse = taskApi.getAllTasks()

        curTaskResponse?.body()?.let {
            taskDao.deleteAllTasks()
            Timber.i("all tasks deleted")
            //insertTasks(tasks.onEach { task -> task.isSynced = true })
        }
    }

    fun getAllTasks(): Flow<Resource<List<Task>>> {
        return networkBoundResource(
            query = {
                taskDao.getAllTasks()
            },
            fetch = {
                syncTasks()
                Timber.i("Tasks synchronized: ${curTaskResponse?.body()?.size}")
                curTaskResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertTasksLocal(it.onEach { task -> task.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    private suspend fun insertTasksLocal(list: List<Task>) {
        list.forEach { task -> insert(task) }
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
            val response = try {
                taskApi.deleteAllCompletedTasks()
            } catch (e: Exception) {
                null
            }
            if (response != null && response.isSuccessful) {
                taskDao.deleteAllCompletedTasks()
            } else {
                val deletedTasks = taskDao.getAllLocallyDeletedTasks()
                insertTasksLocal(deletedTasks.onEach { it.isDeletedLocally = true })
            }
        }
    }

    fun getAllItems(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun updateTask(task: Task) {
        val response = try {
            taskApi.addTask(task)
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            taskDao.update(task)
        } else {
            taskDao.insert(task.apply { !isComplete; isSynced = false })
        }
    }
}