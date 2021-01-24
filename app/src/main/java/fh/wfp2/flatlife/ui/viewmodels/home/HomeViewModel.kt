package fh.wfp2.flatlife.ui.viewmodels.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fh.wfp2.flatlife.data.repositories.ShoppingRepository
import fh.wfp2.flatlife.data.repositories.TaskRepository
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.data.room.entities.Task

class HomeViewModel @ViewModelInject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

   /* private val _allShoppingItems: MutableLiveData<List<ShoppingItem>>
        get() = shoppingRepository.getAllItems().asLiveData() as MutableLiveData<List<ShoppingItem>>

    val allShoppingItems: LiveData<List<ShoppingItem>> = _allShoppingItems*/

    private val _allTaskItems: MutableLiveData<List<Task>>
        get() = taskRepository.getAllItems().asLiveData() as MutableLiveData<List<Task>>

    val allTaskItems: LiveData<List<Task>> = _allTaskItems

}