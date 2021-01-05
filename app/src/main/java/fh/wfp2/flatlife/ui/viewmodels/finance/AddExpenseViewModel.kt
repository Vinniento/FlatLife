package fh.wfp2.flatlife.ui.viewmodels.finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.repositories.AddExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: AddExpenseRepository

    private var addExpenseEventsChannel = Channel<AddExpenseEvents>()
    val addExpenseEvents = addExpenseEventsChannel.receiveAsFlow()

    init {
        val addExpenseDao =
            FlatLifeRoomDatabase.getInstance(application.applicationContext).addExpenseDao()
        repository = AddExpenseRepository(addExpenseDao)
    }

    fun onSaveExpenseClick(activity: FinanceActivity) {
        viewModelScope.launch {
            repository.insert(activity)
            addExpenseEventsChannel.send(AddExpenseEvents.NavigateToFinanceScreen)
        }
    }

    sealed class AddExpenseEvents {
        object NavigateToFinanceScreen : AddExpenseEvents()
    }
}