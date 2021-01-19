package fh.wfp2.flatlife.ui.viewmodels.finance

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.AddExpenseRepository
import fh.wfp2.flatlife.data.repositories.ExpenseCategoryRepository
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel @ViewModelInject constructor(
    private val addExpenseRepository: AddExpenseRepository,
    private val categoryRepository: ExpenseCategoryRepository
) : ViewModel() {

    private var addExpenseEventsChannel = Channel<AddExpenseEvents>()
    val addExpenseEvents = addExpenseEventsChannel.receiveAsFlow()


    private val _allCategories: MutableLiveData<List<String>>
        get() = categoryRepository.getAllItemNames()
            .asLiveData() as MutableLiveData<List<String>>

    val allCategories: LiveData<List<String>> = _allCategories

    fun onSaveExpenseClick(activity: FinanceActivity) {
        viewModelScope.launch {
            addExpenseRepository.insert(activity)
            addExpenseEventsChannel.send(AddExpenseEvents.NavigateToFinanceScreen)
        }
    }

    fun onUpdateExpenseClick(activity: FinanceActivity) {
        viewModelScope.launch {
            addExpenseRepository.update(
                activity.copy(
                    description = activity.description,
                    categoryName = activity.categoryName,
                    price = activity.price
                )
            )
            addExpenseEventsChannel.send(AddExpenseEvents.NavigateToFinanceScreen)

        }
    }

    fun onFinanceActivityArgumentPassed() {
        viewModelScope.launch {

        }
    }

    sealed class AddExpenseEvents {
        object NavigateToFinanceScreen : AddExpenseEvents()
    }
}