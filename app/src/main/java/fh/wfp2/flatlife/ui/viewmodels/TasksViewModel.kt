package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskRoomDatabase
import kotlinx.coroutines.*
import timber.log.Timber


class TasksViewModel(application: Application) : AndroidViewModel(application) {
//class TasksViewModel(application: Application) : ViewModel() {

    private val repository: TaskRepository
    val lastTask: LiveData<Task?>
        get() = lastTaskMutable
    val allTasks: LiveData<List<Task>>
        get() = allTasksMutable

    val lastTaskMutable = MutableLiveData<Task?>()
    private val allTasksMutable = MutableLiveData<List<Task>>()

    private val taskViewModelJob = Job()
    private val uiScope = CoroutineScope(taskViewModelJob + Dispatchers.Main)
    private val ioScope = CoroutineScope(taskViewModelJob + Dispatchers.IO)

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    init {
        val userDao = TaskRoomDatabase.getInstance(application).taskDao()
        repository = TaskRepository(userDao)
        Timber.i("Repository created in viewModel")
        uiScope.launch {
            allTasksMutable.value = getAllTasks()
        }
    }

    fun insert(task: Task) {
        uiScope.launch(errorHandler) {
            repository.insert(task)
            Timber.i("Task added ${task.name}")
            // allTasksMutable.value?.forEach { Log.i("viewModel", "${it.name}") }
        }
    }

    private suspend fun getHighestID(): Task? {
        return withContext(Dispatchers.IO) {
            var task = repository.getHighestTask()
            task
        }

    }

    private suspend fun getAllTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            var allTasks = repository.getAllTasks()
            Timber.i("All tasks retrieved: ${allTasksMutable.value.toString()}")
            allTasks
        }
    }

    /*
    class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    //class TasksViewModel(application: Application) : ViewModel() {

        //private val repository = TaskRepository

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
    */
    override fun onCleared() {
        super.onCleared()
        taskViewModelJob.cancel()
    }

}

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return TasksViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

