package fh.wfp2.flatlife.ui.viewmodels.finance

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.ExpenseCategoryRepository
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FinanceCategoryViewModel @ViewModelInject constructor(
    private val repository: ExpenseCategoryRepository
) :
    ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    private val financeCategoryChannel = Channel<FinanceCategoryEvents>()
    val financeCategoryEvents = financeCategoryChannel.receiveAsFlow()

    private val _allItems = _forceUpdate.switchMap {
        repository.getAllItems().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allItems: LiveData<Event<Resource<List<ExpenseCategory>>>> = _allItems

    fun onAddCategoryClick(expenseCategory: ExpenseCategory) {
        viewModelScope.launch {
            repository.insertItem(expenseCategory)
        }
    }

    fun onCategoryClicked(expenseCategory: ExpenseCategory) {
        viewModelScope.launch {
            financeCategoryChannel.send(
                FinanceCategoryEvents.NavigateToAddExpenseActivityScreen(
                    expenseCategory
                )
            )
        }
    }

    sealed class FinanceCategoryEvents {
        data class NavigateToAddExpenseActivityScreen(val expenseCategory: ExpenseCategory) :
            FinanceCategoryEvents()
    }
}