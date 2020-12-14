package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fh.wfp2.flatlife.data.TaskRepository
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.TaskRoomDatabase
import kotlinx.coroutines.*
import timber.log.Timber


class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

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

    }

    fun insert(task: Task) {
        uiScope.launch(errorHandler) {

        Timber.i("Task added ${task.name}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        taskViewModelJob.cancel()
    }

}

class ShoppingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return ShoppingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

