package fh.wfp2.flatlife.data

import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.data.room.TodoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskRepository(private val taskDao: TaskDao) {
    //object TaskRepository{
    //  private val taskDao: TaskDao

    //val allTasks: Flow<List<Task>> = taskDao.getAllTasks() --> original so aber dann muss man im TasksViewModel .asLiveData() machen, was nicht geht
    //val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val taskRepositoryJob = Job()
    private val ioScope = CoroutineScope(taskRepositoryJob + Dispatchers.IO)

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun getAllTasks(): List<Task> = taskDao.getAllTasks()

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

class TodoRepository(private val todoDao: TodoDao) {
    val todoRepositoryJob = Job()

    private val ioScope = CoroutineScope(todoRepositoryJob + Dispatchers.IO)

    suspend fun insert(todo: Todo) {
        ioScope.launch {
            todoDao.insert(todo)
        }
    }

    fun getTodos(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Todo>> {
        Timber.i("getTodos called")

        val todos = todoDao.getTodos(searchQuery, hideCompleted, sortOrder)

        return todos
    }

    fun getTodoWithHighestID(): Todo = todoDao.getTodoWithHighestID()

    suspend fun update(todo: Todo) {
        ioScope.launch {
            todoDao.update(todo.copy(isComplete = todo.isComplete))
        }
    }

    suspend fun delete(todo: Todo) {
        ioScope.launch {

            todoDao.delete(todo)
        }
    }
}

