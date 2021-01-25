package fh.wfp2.flatlife.ui.viewmodels.shopping

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.ShoppingRepository
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditShoppingItemFragmentViewModel @ViewModelInject constructor(
    private val repository: ShoppingRepository
) :
    ViewModel() {

    private val editShopppingItemEventChannel = Channel<EditShoppingItemEvent>()
    val editShoppingItemEvent = editShopppingItemEventChannel.receiveAsFlow()

    private val state = SavedStateHandle()

    var _item = MutableLiveData<ShoppingItem>()

    val item: LiveData<ShoppingItem>
        get() = _item

    fun onArgumentsPassed(shoppingItem: ShoppingItem) {
        _item.value = shoppingItem
    }

    fun onUpdateItemClick(itemName: String, isBought: Boolean) {
        when {
            itemName.isEmpty() -> viewModelScope.launch {
                editShopppingItemEventChannel.send(EditShoppingItemEvent.ShowIncompleteShoppingItemMessage)
            }
            itemName.isNotEmpty() -> GlobalScope.launch {
                val id = _item.value?.id ?: 0

                repository.insertItem(ShoppingItem(id = id, name = itemName, isBought = isBought))
                editShopppingItemEventChannel.send(EditShoppingItemEvent.NavigateToShoppingFragmentScreen)
            }
        }
    }


    sealed class EditShoppingItemEvent {
        object NavigateToShoppingFragmentScreen : EditShoppingItemEvent()
        object ShowIncompleteShoppingItemMessage : EditShoppingItemEvent()
    }
}