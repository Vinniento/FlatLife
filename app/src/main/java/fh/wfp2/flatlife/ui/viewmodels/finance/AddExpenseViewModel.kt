package fh.wfp2.flatlife.ui.viewmodels.finance

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.repositories.AddExpenseRepository
import fh.wfp2.flatlife.data.room.repositories.ExpenseCategoryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: AddExpenseRepository
    private var categoryRepository: ExpenseCategoryRepository
    private var addExpenseEventsChannel = Channel<AddExpenseEvents>()
    val addExpenseEvents = addExpenseEventsChannel.receiveAsFlow()

    init {
        val db = FlatLifeRoomDatabase.getInstance(application.applicationContext)
        val addExpenseDao =
            db.addExpenseDao()
        repository = AddExpenseRepository(addExpenseDao)

        val expenseCategoryDao = db.expenseCategoryDao()
        categoryRepository = ExpenseCategoryRepository(expenseCategoryDao)
    }

    private val _allCategories: MutableLiveData<List<String>>
        get() = categoryRepository.getAllItemNames()
            .asLiveData() as MutableLiveData<List<String>>

    val allCategories: LiveData<List<String>> = _allCategories

    fun onSaveExpenseClick(activity: FinanceActivity) {
        viewModelScope.launch {
            repository.insert(activity)
            addExpenseEventsChannel.send(AddExpenseEvents.NavigateToFinanceScreen)
        }
    }

    fun onUpdateExpenseClick(activity: FinanceActivity) {
        viewModelScope.launch {
            repository.update(
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