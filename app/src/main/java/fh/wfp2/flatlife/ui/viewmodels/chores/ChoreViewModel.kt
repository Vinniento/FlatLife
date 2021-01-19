package fh.wfp2.flatlife.ui.viewmodels.chores

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.daos.ChoresDao
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.data.room.repositories.ChoresRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChoreViewModel(application: Application) : AndroidViewModel(application) {


    private var choresDao: ChoresDao = FlatLifeRoomDatabase.getInstance(application).choresDao()
    private var repository: ChoresRepository = ChoresRepository(choresDao)

    private val _allChores: MutableLiveData<List<Chore>>
        get() = repository.getAllChores().asLiveData() as MutableLiveData<List<Chore>>
    val allChores: LiveData<List<Chore>> = _allChores

    val choreDate = MutableLiveData<String>()
    private val choreViewModelChannel = Channel<ChoreViewModelEvents>()
    val choreViewModelEvent = choreViewModelChannel.receiveAsFlow()
    fun addChore(chore: Chore) {
        viewModelScope.launch {
            repository.insert(chore)
        }
    }

    fun onItemClick(chore: Chore) {
        viewModelScope.launch {
            choreViewModelChannel.send(ChoreViewModelEvents.NavigateToEditChoreScreen(chore))
        }
    }

    fun onCheckBoxClicked(chore: Chore, checked: Boolean) {
        viewModelScope.launch {
            repository.update(chore.copy(isComplete = checked))
        }
    }

    fun onUpdateChoreClick(chore: Chore) {
        viewModelScope.launch {
            repository.update(chore)
        }
    }

    sealed class ChoreViewModelEvents {
        data class NavigateToEditChoreScreen(val chore: Chore) : ChoreViewModelEvents()

    }
}