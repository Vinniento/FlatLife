package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.ShoppingRepository
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber


class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShoppingRepository

    private val shoppingViewModelJob = Job()
    private val uiScope = CoroutineScope(shoppingViewModelJob + Dispatchers.Main)


    private val addShoppingItemChannel = Channel<ShoppingEvents>()
    val addShoppingItemEvents = addShoppingItemChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    //TODO ultra hässlich, wie macht man das schöner? mit switchMap oder so?
    private val _allItems: MutableLiveData<List<ShoppingItem>>
        get() = repository.getAllItems().asLiveData() as MutableLiveData<List<ShoppingItem>>


    init {
        val shoppingDao = FlatLifeRoomDatabase.getInstance(application).shoppingDao()
        repository = ShoppingRepository(shoppingDao)
        viewModelScope.launch {

        }
        Timber.i("Repository created in viewModel")

    }

    val allItems: LiveData<List<ShoppingItem>> = _allItems

    fun onAddItemClick(itemName: String) {
        if (itemName.isEmpty())
            viewModelScope.launch {
                addShoppingItemChannel.send(ShoppingEvents.ShowIncompleteItemMessage)
            }
        else
            viewModelScope.launch {
                repository.insert(ShoppingItem(name = itemName))
                addShoppingItemChannel.send(ShoppingEvents.ShowItemAddedMessage(itemName))
            }
    }


    override fun onCleared() {
        super.onCleared()
        shoppingViewModelJob.cancel()
    }

    fun onShoppingItemSelected(item: ShoppingItem) {
        viewModelScope.launch {

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
            repository.update(item.copy(isBought = isChecked))
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

