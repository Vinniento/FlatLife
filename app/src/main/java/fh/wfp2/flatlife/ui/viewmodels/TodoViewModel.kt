package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.TodoRepository
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.data.room.TodoRoomDatabase
import kotlinx.coroutines.*
import timber.log.Timber


class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository
    val latestTodo: LiveData<Todo?>
        get() = lastTodoMutable
    val allTodos: LiveData<List<Todo>>
        get() = allTodosMutable

    val lastTodoMutable = MutableLiveData<Todo?>()
    private val allTodosMutable = MutableLiveData<List<Todo>>()

    private val taskViewModelJob = Job()
    private val uiScope = CoroutineScope(taskViewModelJob + Dispatchers.Main)
    private val ioScope = CoroutineScope(taskViewModelJob + Dispatchers.IO)

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    init {
        val userDao = TodoRoomDatabase.getInstance(application).todoDao()
        repository = TodoRepository(userDao)
        Timber.i("Repository created in viewModel")
        uiScope.launch {
            allTodosMutable.value = getAllTodos()
        }
    }

    fun insert(todo: Todo) {
        uiScope.launch(errorHandler) {
            repository.insert(todo)
            Timber.i("Task added ${todo.name}")
            // allTasksMutable.value?.forEach { Log.i("viewModel", "${it.name}") }
        }
    }

    private suspend fun getTodoWithHighestID(): Todo? {
        return withContext(Dispatchers.IO) {
            var todo = repository.getTodoWithHighestID()
            todo
        }

    }

    private suspend fun getAllTodos(): List<Todo> {
        return withContext(Dispatchers.IO) {
            var allTasks = repository.getAllTodos()
            Timber.i("All tasks retrieved: ${allTodosMutable.value.toString()}")
            allTasks
        }
    }


    override fun onCleared() {
        super.onCleared()
        taskViewModelJob.cancel()
    }

}

class TodoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return TodoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

