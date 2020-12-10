package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import fh.wfp2.flatlife.data.TodoRepository
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.data.room.TodoDao
import fh.wfp2.flatlife.data.room.TodoRoomDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber


@ExperimentalCoroutinesApi
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository
    private var todoDao: TodoDao = TodoRoomDatabase.getInstance(application).todoDao()
    private val taskViewModelJob = Job()

    private val uiScope = CoroutineScope(taskViewModelJob + Dispatchers.Main)
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    //MutableStateFlow: Sobald sich etwas ändert, wird die entsprechende DB query abgesetzt
    val searchQuery = MutableStateFlow("")
    val hideCompleted = MutableStateFlow(false)
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)


    init {
        repository = TodoRepository(todoDao)
        Timber.i("Repository created in viewModel")

    }

    private val todosFlow = combine(searchQuery, hideCompleted, sortOrder)
    { query, hideCompleted, sortOrder ->
        Triple(
            query,
            hideCompleted,
            sortOrder
        ) //angeblich CustomClass besser falls sich was ändert
    }
        .flatMapLatest { (query, hideCompleted, sortOrder) ->
            repository.getTodos(query, hideCompleted, sortOrder)
        }

    val todos = todosFlow.asLiveData()


    fun insert(todo: Todo) {
        uiScope.launch(errorHandler) {
            repository.insert(todo)
            Timber.i("Task added ${todo.name}")
            // allTodosMutable?.let {  it -> it.value.forEach { Log.i("viewModel", "${it.name}") }} -> wieso brauch ich hier tdm ein ? bei der foreach obwohl ich ?.let mach?
        }
    }


    fun onCheckedPressed(todos: Todo) {
        uiScope.launch(errorHandler) {
            repository.update(todos)
            Timber.i("todo updated")
        }
    }

/* private suspend fun getTodos(): List<Todo> {
     return withContext(Dispatchers.Main) {
         val allTasks = repository.getTodos()
         Timber.i("All todos retrieved: ${allTodosMutable.value.toString()}")
         allTasks.sortedBy {
             it.todoId
         }
     }
 }*/


    override fun onCleared() {
        super.onCleared()
        taskViewModelJob.cancel()
    }

}

data class customTriple(
    val searchQuery: String,
    val hideCompleted: Boolean,
    val sortOrder: SortOrder
)

enum class SortOrder { BY_NAME, BY_DATE }
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

