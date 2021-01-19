package fh.wfp2.flatlife.ui.viewmodels.shopping

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.ShoppingRepository
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditShoppingItemFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val editShopppingItemEventChannel = Channel<EditShoppingItemEvent>()
    val editShoppingItemEvent = editShopppingItemEventChannel.receiveAsFlow()

    private val state = SavedStateHandle()
    private val repository: ShoppingRepository

    var _item = MutableLiveData<ShoppingItem>()

    val item: LiveData<ShoppingItem>
        get() = _item

    init {
        val shoppingDao = FlatLifeRoomDatabase.getInstance(application).shoppingDao()
        repository = ShoppingRepository(shoppingDao)
    };

    fun onArgumentsPassed(shoppingItem: ShoppingItem) {
        _item.value = shoppingItem
    }

    fun onUpdateItemClick(itemName: String, isBought: Boolean) {
        when {
            itemName.isEmpty() -> viewModelScope.launch {
                editShopppingItemEventChannel.send(EditShoppingItemEvent.ShowIncompleteShoppingItemMessage)
            }
            _item.value == null && itemName.isNotEmpty() -> viewModelScope.launch {
                repository.insert(ShoppingItem(name = itemName, isBought = isBought))
                editShopppingItemEventChannel.send(EditShoppingItemEvent.NavigateToShoppingFragmentScreen)
            }
            else -> _item.value?.let {
                viewModelScope.launch {
                    repository.update(it.copy(name = itemName, isBought = isBought))
                    editShopppingItemEventChannel.send(EditShoppingItemEvent.NavigateToShoppingFragmentScreen)
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

    sealed class EditShoppingItemEvent {
        object NavigateToShoppingFragmentScreen : EditShoppingItemEvent()
        object ShowIncompleteShoppingItemMessage : EditShoppingItemEvent()
    }
}