package fh.wfp2.flatlife.ui.viewmodels.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.TaskRepository
import fh.wfp2.flatlife.data.room.entities.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddTaskFragmentViewModel @ViewModelInject constructor(private val repository: TaskRepository) : ViewModel() {

    private val addTasksEventChannel = Channel<AddTaskEvent>()
    val addTasksEvent = addTasksEventChannel.receiveAsFlow()

    private val state = SavedStateHandle()

    var _task = MutableLiveData<Task>()

    val task: LiveData<Task>
        get() = _task

    fun onArgumentsPassed(task: Task) {
        _task.value = task
    }

    fun onAddTaskClick(task: Task) {
        when {
            task.name.isEmpty() -> viewModelScope.launch {
                addTasksEventChannel.send(AddTaskEvent.ShowIncompleteTaskMessage)
            }
            _task.value == null && task.name.isNotEmpty() -> viewModelScope.launch {
                repository.insertTask(task)
                addTasksEventChannel.send(AddTaskEvent.NavigateToTaskFragmentScreen)
            }
            else -> _task.value?.let {
                viewModelScope.launch {
                    repository.update(it.copy(name = task.name, isImportant = task.isImportant))
                    addTasksEventChannel.send(AddTaskEvent.NavigateToTaskFragmentScreen)
                }

            }
        }
    }

/* private val state = SavedStateHandle()
     val task = state.get<Task>("task")

     var taskName = state.get<String>("taskName") ?: task?.name ?: ""
         set(value) {
             field = value
             state.set("taskName", value)
         }
     var taskImportance = state.get<Boolean>("taskImportance") ?: task?.isImportant ?: false
         set(value) {
             field = value
             state.set("taskImportance", value)
         }
 */

    sealed class AddTaskEvent {
        object NavigateToTaskFragmentScreen : AddTaskEvent()
        object ShowIncompleteTaskMessage : AddTaskEvent()
    }
}