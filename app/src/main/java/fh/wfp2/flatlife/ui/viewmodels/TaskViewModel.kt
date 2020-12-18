package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.preferences.PreferencesManager
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskDao
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber


@ExperimentalCoroutinesApi
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private var taskDao: TaskDao = FlatLifeRoomDatabase.getInstance(application).taskDao()
    private val taskViewModelJob = Job()

    private val uiScope = CoroutineScope(taskViewModelJob + Dispatchers.Main)

    private val tasksEventChannel = Channel<TaskEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    private val preferencesManager = PreferencesManager(application)

    //MutableStateFlow: Sobald sich etwas ändert, wird die entsprechende DB query abgesetzt
    val searchQuery = MutableStateFlow("")
    val preferencesFlow = preferencesManager.preferencesFlow

    init {
        repository = TaskRepository(taskDao)
        Timber.i("Repository created in viewModel")

    }

    private val tasksFlow = combine(searchQuery, preferencesFlow)
    { query, preferencesFlow ->
        Pair(
            query,
            preferencesFlow
        ) //angeblich CustomClass besser falls sich was ändert
    }
        .flatMapLatest { (query, filterPreferences) ->
            repository.getTasks(query, filterPreferences.hideCompleted, filterPreferences.sortOrder)
        }

    val tasks = tasksFlow.asLiveData()

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


    fun onAddTaskClick(task: Task) {
        uiScope.launch(errorHandler) {
            repository.insert(Task(name = task.name, isImportant = task.isImportant))
            Timber.i("Task added ${task.name}  : ${task.isImportant}")
        }
    }


    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckChanged(task: Task, isChecked: Boolean) = uiScope.launch(errorHandler) {
        repository.update(task.copy(isComplete = isChecked))
        Timber.i("CheckedState : $isChecked")
    }

    fun onTaskChanged(task: Task) = uiScope.launch(errorHandler) {
        repository.update(task)
        Timber.i("New Todo : $task")
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

    fun onSwipedRight(task: Task) {
        uiScope.launch(errorHandler) {

            repository.deleteTask(task)
            tasksEventChannel.send(
                TaskEvent.ShowUndoDeleteTaskMessage(task)
            )
        }
    }

    fun deleteAllCompletedTasks() {
        uiScope.launch(errorHandler) {
            repository.deleteAllCompletedTasks()

        }
    }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch(errorHandler) {
            repository.insert(task)
        }
    }

    //benefit of sealed class -> when checking with when (){} the compiler knows if the list checked is exhaustive or not
    sealed class TaskEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }

}

data class customTriple(
    val searchQuery: String,
    val hideCompleted: Boolean,
    val sortOrder: SortOrder
)

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }


}

