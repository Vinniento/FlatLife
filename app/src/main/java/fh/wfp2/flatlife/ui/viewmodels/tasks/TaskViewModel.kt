package fh.wfp2.flatlife.ui.viewmodels.tasks

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.preferences.PreferencesManager
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.repositories.TaskRepository
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

@ExperimentalCoroutinesApi
class TaskViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    application: Application
) : ViewModel() {

    private val taskViewModelJob = Job()
    private val state = SavedStateHandle()
    private val _forceUpdate = MutableLiveData(false)

    private val uiScope = CoroutineScope(taskViewModelJob + Dispatchers.Main)

    private val tasksEventChannel = Channel<TaskEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    private val preferencesManager = PreferencesManager(application)

    //MutableStateFlow: Sobald sich etwas ändert, wird die entsprechende DB query abgesetzt
    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val _allTasks = _forceUpdate.switchMap {
        repository.getAllTasks().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allTasks: LiveData<Event<Resource<List<Task>>>> = _allTasks

    private val tasksFlow = combine(searchQuery.asFlow(), preferencesFlow)
    { query, preferencesFlow ->
        Pair(
            query,
            preferencesFlow
        ) //angeblich CustomClass besser falls sich was ändert
    }.flatMapLatest { (query, filterPreferences) ->
        repository.getTasks(query, filterPreferences.hideCompleted, filterPreferences.sortOrder)
    }

    val tasks = tasksFlow.asLiveData()
    fun syncAllTasks() = _forceUpdate.postValue(true)

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

    fun onTaskCheckChanged(task: Task) = uiScope.launch(errorHandler) {
        repository.insertTask(task.copy(id = task.id, isComplete = task.isComplete))
        Timber.i("CheckedState : ${task.isComplete}")
    }

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
            repository.insertTask(task)
        }
    }

    fun onAddNewTaskClick() = viewModelScope.launch {

        //repository.insertTask(Task(0, "bla", false, 2334564545, true))
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    //benefit of sealed class -> when checking with when (){} the compiler knows if the list checked is exhaustive or not
    sealed class TaskEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()

        //only one instance is created
        object NavigateToAddTaskScreen : TaskEvent()
    }

    data class customTriple(
        val searchQuery: String,
        val hideCompleted: Boolean,
        val sortOrder: SortOrder
    )
}