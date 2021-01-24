package fh.wfp2.flatlife.ui.viewmodels.finance

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.FinanceActivityRepository
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FinanceActivityViewModel @ViewModelInject constructor(private val financeActivityRepository: FinanceActivityRepository) :
    ViewModel() {

    private val financeActivityChannel = Channel<FinanceActivityEvents>()
    val financeActivityEvents = financeActivityChannel.receiveAsFlow()

    private val _allItems: MutableLiveData<List<FinanceActivity>>
        get() = financeActivityRepository.getAllActivities()
            .asLiveData() as MutableLiveData<List<FinanceActivity>>

    val allItems: LiveData<List<FinanceActivity>> = _allItems

    fun onActivityClicked(financeActivity: FinanceActivity) {
        viewModelScope.launch {
            financeActivityChannel.send(
                FinanceActivityEvents.NavigateToAddExpenseActivityScreen(
                    financeActivity
                )
            )
        }
    }

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


    sealed class FinanceActivityEvents {
        object NavigateToAddExpenseCategoryScreen : FinanceActivityEvents()
        data class NavigateToAddExpenseActivityScreen(val financeActivity: FinanceActivity) :
            FinanceActivityEvents()

        object NavigateToBalanceScreen : FinanceActivityEvents()
    }
}