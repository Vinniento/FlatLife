package fh.wfp2.flatlife.ui.viewmodels.finance

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.FinanceActivityRepository
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FinanceActivityViewModel @ViewModelInject constructor(
    private val repository: FinanceActivityRepository
) :
    ViewModel() {

    private val financeActivityChannel = Channel<FinanceActivityEvents>()
    val financeActivityEvents = financeActivityChannel.receiveAsFlow()

    private val _forceUpdate = MutableLiveData(false)

    private val _allItems = _forceUpdate.switchMap {
        repository.getAllActivities().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allItems: LiveData<Event<Resource<List<FinanceActivity>>>> = _allItems

    fun onActivityClicked(financeActivity: FinanceActivity) {
        viewModelScope.launch {
            financeActivityChannel.send(
                FinanceActivityEvents.NavigateToAddExpenseActivityScreen(
                    financeActivity
                )
            )
        }
    }

    fun syncAllItems() = _forceUpdate.postValue(true)

    fun onAddActivityClick() {
        viewModelScope.launch {
            financeActivityChannel.send(FinanceActivityEvents.NavigateToAddExpenseCategoryScreen)
        }
    }

    fun onBalanceButtonClick() {
        viewModelScope.launch {
            financeActivityChannel.send(FinanceActivityEvents.NavigateToBalanceScreen)
        }
    }

    fun onSwipedRight(item: FinanceActivity) {
        viewModelScope.launch {
            repository.deleteItem(item)
            financeActivityChannel.send(
                FinanceActivityEvents.ShowUndoDeleteActivityMessage(item)
            )
        }
    }

    fun undoDeleteClick(item: FinanceActivity) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    sealed class FinanceActivityEvents {
        object NavigateToAddExpenseCategoryScreen : FinanceActivityEvents()
        data class NavigateToAddExpenseActivityScreen(val financeActivity: FinanceActivity) :
            FinanceActivityEvents()

        data class ShowUndoDeleteActivityMessage(val item: FinanceActivity) :
            FinanceActivityEvents()

        object NavigateToBalanceScreen : FinanceActivityEvents()
    }
}