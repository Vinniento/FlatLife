package fh.wfp2.flatlife.data

import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.data.room.TodoDao

class TaskRepository(private val taskDao: TaskDao) {
    //object TaskRepository{
    //  private val taskDao: TaskDao

    //val allTasks: Flow<List<Task>> = taskDao.getAllTasks() --> original so aber dann muss man im TasksViewModel .asLiveData() machen, was nicht geht
    //val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

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

    suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    suspend fun getAllTodos(): List<Todo> = todoDao.getAllTodos()

    fun getTodoWithHighestID(): Todo = todoDao.getTodoWithHighestID()

}

