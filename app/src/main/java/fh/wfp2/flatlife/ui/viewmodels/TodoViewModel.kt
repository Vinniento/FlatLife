package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.TodoRepository
import fh.wfp2.flatlife.data.preferences.PreferencesManager
import fh.wfp2.flatlife.data.preferences.SortOrder
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

    private val preferencesManager = PreferencesManager(application)

    //MutableStateFlow: Sobald sich etwas ändert, wird die entsprechende DB query abgesetzt
    val searchQuery = MutableStateFlow("")
    val preferencesFlow = preferencesManager.preferencesFlow

    init {
        repository = TodoRepository(todoDao)
        Timber.i("Repository created in viewModel")

    }

    private val todosFlow = combine(searchQuery, preferencesFlow)
    { query, preferencesFlow ->
        Pair(
            query,
            preferencesFlow
        ) //angeblich CustomClass besser falls sich was ändert
    }
        .flatMapLatest { (query, filterPreferences) ->
            repository.getTodos(query, filterPreferences.hideCompleted, filterPreferences.sortOrder)
        }

    val todos = todosFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onHideCompletedClick(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }


    fun insert(todo: Todo) {
        uiScope.launch(errorHandler) {
            repository.insert(todo)
            Timber.i("Task added ${todo.name}")
            // allTodosMutable?.let {  it -> it.value.forEach { Log.i("viewModel", "${it.name}") }} -> wieso brauch ich hier tdm ein ? bei der foreach obwohl ich ?.let mach?
        }
    }


    fun onTaskSelected(todo: Todo) {

    }

    fun onTodoCheckChanged(todo: Todo, isChecked: Boolean) = uiScope.launch(errorHandler) {
        repository.update(todo.copy(isComplete = isChecked))
        Timber.i("CheckedState : $isChecked")
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

    fun onSwipedRight(todo: Todo) {
        uiScope.launch(errorHandler) {

            repository.delete(todo)
        }
    }

    fun deleteAllCompletedTodos() {
        uiScope.launch(errorHandler) {
            repository.deleteAllCompletedTodos()

        }
    }

}

data class customTriple(
    val searchQuery: String,
    val hideCompleted: Boolean,
    val sortOrder: SortOrder
)

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

