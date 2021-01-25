package fh.wfp2.flatlife.ui.viewmodels.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.data.repositories.TaskRepository
import fh.wfp2.flatlife.data.room.entities.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskFragmentViewModel @ViewModelInject constructor(private val repository: TaskRepository) :
    ViewModel() {

    private val addTasksEventChannel = Channel<AddTaskEvent>()
    val addTasksEvent = addTasksEventChannel.receiveAsFlow()

    private val state = SavedStateHandle()

    private val _task = MutableLiveData<Event<Resource<Task>>>()
    val task: LiveData<Event<Resource<Task>>> = _task

    fun onAddTaskClick(task: Task) {
        when {
            task.name.isEmpty() -> viewModelScope.launch {
                addTasksEventChannel.send(AddTaskEvent.ShowIncompleteTaskMessage)
            }
            task.name.isNotEmpty() -> GlobalScope.launch {
                addTasksEventChannel.send(AddTaskEvent.NavigateToTaskFragmentScreen)
                repository.insertTask(task)
            }

        }
    }

    fun onArgumentsPassed(task: Task) {
        _task.postValue(Event((Resource.success(task))))
    }

    sealed class AddTaskEvent {
        object NavigateToTaskFragmentScreen : AddTaskEvent()
        object ShowIncompleteTaskMessage : AddTaskEvent()
    }
}