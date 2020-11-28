package fh.wfp2.flatlife.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.room.Task
import kotlinx.coroutines.launch

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<Task>> =
        repository.allTasks //.asLiveData() muss man mit extension functions machen?

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
