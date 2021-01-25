package fh.wfp2.flatlife.ui.viewmodels.finance

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fh.wfp2.flatlife.data.repositories.ExpenseCategoryRepository
import fh.wfp2.flatlife.data.repositories.FinanceActivityRepository
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel @ViewModelInject constructor(
    private val repository: FinanceActivityRepository,
    private val categoryRepository: ExpenseCategoryRepository
) : ViewModel() {

    private var addExpenseEventsChannel = Channel<AddExpenseEvents>()
    val addExpenseEvents = addExpenseEventsChannel.receiveAsFlow()

    private val _allCategories: MutableLiveData<List<String>>
        get() = categoryRepository.getAllItemNames()
            .asLiveData() as MutableLiveData<List<String>>

    val allCategories: LiveData<List<String>> = _allCategories

    fun onSaveExpenseClick(activity: FinanceActivity) {
        GlobalScope.launch {
            addExpenseEventsChannel.send(AddExpenseEvents.NavigateToFinanceScreen)
            repository.insertItem(activity)
        }
    }

    sealed class AddExpenseEvents {
        object NavigateToFinanceScreen : AddExpenseEvents()
    }
}