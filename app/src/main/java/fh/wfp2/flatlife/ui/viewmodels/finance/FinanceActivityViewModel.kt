package fh.wfp2.flatlife.ui.viewmodels.finance

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.daos.FinanceActivityDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.repositories.FinanceActivityRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class FinanceActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: FinanceActivityRepository
    private var financeActivityDao: FinanceActivityDao

    private val financeActivityChannel = Channel<FinanceActivityEvents>()
    val financeActivityEvents = financeActivityChannel.receiveAsFlow()

    private val _allItems: MutableLiveData<List<FinanceActivity>>
        get() = repository.getAllActivities().asLiveData() as MutableLiveData<List<FinanceActivity>>

    init {
        financeActivityDao = FlatLifeRoomDatabase.getInstance(application).financeActivityDao()
        repository = FinanceActivityRepository(financeActivityDao)
    }

    val allItems: LiveData<List<FinanceActivity>> = _allItems

    fun onActivityClicked(financeActivity: FinanceActivity) {
        viewModelScope.launch {
            financeActivityChannel.send(FinanceActivityEvents.NavigateToAddExpenseActivityScreen(financeActivity))
        }
    }

    fun onAddActivityClick() {
        viewModelScope.launch {
            financeActivityChannel.send(FinanceActivityEvents.NavigateToAddExpenseCategoryScreen)
        }
    }


    sealed class FinanceActivityEvents {
        object NavigateToAddExpenseCategoryScreen : FinanceActivityEvents()
        data class NavigateToAddExpenseActivityScreen(val financeActivity: FinanceActivity) :
            FinanceActivityEvents()
    }
}

class FinanceActivityViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return FinanceActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}