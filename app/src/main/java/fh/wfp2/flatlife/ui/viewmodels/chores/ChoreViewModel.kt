package fh.wfp2.flatlife.ui.viewmodels.chores

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.repositories.ChoresRepository
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.other.Event
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChoreViewModel @ViewModelInject constructor(private val repository: ChoresRepository) :
    ViewModel() {

    private val choreViewModelChannel = Channel<ChoreViewModelEvents>()
    val choreViewModelEvent = choreViewModelChannel.receiveAsFlow()
    private val _forceUpdate = MutableLiveData(false)

    private val _allItems = _forceUpdate.switchMap {
        repository.getAllChores().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allItems: LiveData<Event<Resource<List<Chore>>>> = _allItems

    val choreDate = MutableLiveData<String>()

    fun syncAllItems() = _forceUpdate.postValue(true)


    fun addChore(chore: Chore) {
        viewModelScope.launch {
            repository.insertItem(chore)
        }
    }

    fun onItemClick(chore: Chore) {
        viewModelScope.launch {
            choreViewModelChannel.send(ChoreViewModelEvents.NavigateToEditChoreScreen(chore))
        }
    }

    fun onCheckBoxClicked(chore: Chore, checked: Boolean) {
        viewModelScope.launch {
            repository.insertItem(chore.copy(isComplete = checked))
        }
    }


    fun onSwipedRight(item: Chore) {
        viewModelScope.launch {
            repository.deleteItem(item)
            choreViewModelChannel.send(
                ChoreViewModelEvents.ShowUndoDeleteChoreMessage(item)
            )
        }
    }

    fun undoDeleteClick(item: Chore) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }


    sealed class ChoreViewModelEvents {
        data class NavigateToEditChoreScreen(val chore: Chore) : ChoreViewModelEvents()
        data class ShowUndoDeleteChoreMessage(val chore: Chore) : ChoreViewModelEvents()
    }
}