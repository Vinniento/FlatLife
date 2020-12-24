package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AddTaskFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val addTasksEventChannel = Channel<AddTaskFragmentViewModel.AddTaskEvent>()
    val addTasksEvent = addTasksEventChannel.receiveAsFlow()

    private val state = SavedStateHandle()
    private val repository: TaskRepository

    private var _task = MutableLiveData<Task>()

    val task: LiveData<Task> = _task

    init {
        val taskDao = FlatLifeRoomDatabase.getInstance(application).taskDao()
        repository = TaskRepository(taskDao)


    };

    fun onArgumentsPassed(taskId: Long) {
        if (taskId.toInt() != -1) {
            viewModelScope.launch {
                _task.value = repository.getTaskById(taskId).asLiveData().value

                _task.value?.let {

                    Timber.i("Id:${it.id}, taskName : ${it.name}")
                }

            }

        }

    }

    fun onAddTaskClick(taskName: String, isImportant: Boolean) {

        if (taskName.isNotEmpty())
            viewModelScope.launch {
                repository.insert(Task(name = taskName, isImportant = isImportant))
                addTasksEventChannel.send(AddTaskEvent.NavigateToTaskFragmentScreen)
            }
        else {
            viewModelScope.launch {
                addTasksEventChannel.send(AddTaskEvent.ShowIncompleteTaskMessage)
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