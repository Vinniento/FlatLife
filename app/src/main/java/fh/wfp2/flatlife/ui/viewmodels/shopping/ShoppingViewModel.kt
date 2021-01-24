package fh.wfp2.flatlife.ui.viewmodels.shopping

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.ShoppingRepository
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber


class ShoppingViewModel @ViewModelInject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val shoppingViewModelJob = Job()
    private val uiScope = CoroutineScope(shoppingViewModelJob + Dispatchers.Main)
    private val _forceUpdate = MutableLiveData(false)

    private val addShoppingItemChannel = Channel<ShoppingEvents>()
    val addShoppingItemEvents = addShoppingItemChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    private val _allItems = _forceUpdate.switchMap {
        repository.getAllItems().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allItems: LiveData<Event<Resource<List<ShoppingItem>>>> = _allItems

    fun syncAllTasks() = _forceUpdate.postValue(true)

    fun onAddItemClick(itemName: String) {
        if (itemName.isEmpty())
            viewModelScope.launch {
                addShoppingItemChannel.send(ShoppingEvents.ShowIncompleteItemMessage)
            }
        else
            viewModelScope.launch {
                repository.insertItem(ShoppingItem(name = itemName))
                addShoppingItemChannel.send(ShoppingEvents.ShowItemAddedMessage(itemName))
            }
    }

    override fun onCleared() {
        super.onCleared()
        shoppingViewModelJob.cancel()
    }

    fun onShoppingItemSelected(item: ShoppingItem) {
        viewModelScope.launch {
            addShoppingItemChannel.send(ShoppingEvents.NavigateToEditShoppingItemFragment(item))
        }
    }

    fun deleteAllBoughtItems() {
        viewModelScope.launch {
            repository.deleteAllBoughtItems()
        }
    }

    fun undoDeleteClick(item: ShoppingItem) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun onShoppingItemCheckedChanged(item: ShoppingItem, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isBought = isChecked))
            Timber.i("Item checked updated: isChecked = $isChecked")
        }
    }

    fun onSwipedRight(item: ShoppingItem) {
        uiScope.launch(errorHandler) {
            repository.deleteItem(item)
            addShoppingItemChannel.send(
                ShoppingEvents.ShowUndoDeleteTaskMessage(item)
            )
        }
    }

    sealed class ShoppingEvents {
        object ShowIncompleteItemMessage : ShoppingEvents()
        data class ShowItemAddedMessage(val item: String) : ShoppingEvents()
        data class ShowUndoDeleteTaskMessage(val item: ShoppingItem) : ShoppingEvents()
        data class NavigateToEditShoppingItemFragment(val item: ShoppingItem) : ShoppingEvents()
    }
}