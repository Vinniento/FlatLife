package fh.wfp2.flatlife.ui.viewmodels.finance

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.daos.ExpenseCategoryDao
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.repositories.ExpenseCategoryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FinanceCategoryViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: ExpenseCategoryRepository
    private var categoryDao: ExpenseCategoryDao

    private val financeCategoryChannel = Channel<FinanceCategoryEvents>()
    val financeCategoryEvents = financeCategoryChannel.receiveAsFlow()

    private val _allItems: MutableLiveData<List<ExpenseCategory>>
        get() = repository.getAllItems().asLiveData() as MutableLiveData<List<ExpenseCategory>>

    init {
        categoryDao = FlatLifeRoomDatabase.getInstance(application).expenseCategoryDao()
        repository = ExpenseCategoryRepository(categoryDao)
    }

    val allItems: LiveData<List<ExpenseCategory>> = _allItems

    fun onAddCategoryClick(expenseCategory: ExpenseCategory) {
        viewModelScope.launch {
            repository.insert(expenseCategory)
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