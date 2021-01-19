package fh.wfp2.flatlife.ui.viewmodels.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.data.room.repositories.ShoppingRepository
import fh.wfp2.flatlife.data.room.repositories.TaskRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var taskRepository: TaskRepository
    private var shoppingRepository: ShoppingRepository

    init {
        val taskDao = FlatLifeRoomDatabase.getInstance(application).taskDao()
        taskRepository = TaskRepository(taskDao)
        val shoppingDao = FlatLifeRoomDatabase.getInstance(application).shoppingDao()
        shoppingRepository = ShoppingRepository(shoppingDao)

    }

    private val _allShoppingItems: MutableLiveData<List<ShoppingItem>>
        get() = shoppingRepository.getAllItems().asLiveData() as MutableLiveData<List<ShoppingItem>>

    val allShoppingItems: LiveData<List<ShoppingItem>> = _allShoppingItems

    private val _allTaskItems: MutableLiveData<List<Task>>
        get() = taskRepository.getAllItems().asLiveData() as MutableLiveData<List<Task>>

    val allTaskItems: LiveData<List<Task>> = _allTaskItems

}