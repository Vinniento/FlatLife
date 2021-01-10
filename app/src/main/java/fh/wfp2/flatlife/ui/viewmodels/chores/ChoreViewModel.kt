package fh.wfp2.flatlife.ui.viewmodels.chores

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.daos.ChoresDao
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.data.room.repositories.ChoresRepository
import kotlinx.coroutines.launch

class ChoreViewModel(application: Application) : AndroidViewModel(application) {


    private var choresDao: ChoresDao = FlatLifeRoomDatabase.getInstance(application).choresDao()
    private var repository: ChoresRepository = ChoresRepository(choresDao)

    private val _allChores: MutableLiveData<List<Chore>>
        get() = repository.getAllChores().asLiveData() as MutableLiveData<List<Chore>>
    val allChores: LiveData<List<Chore>> = _allChores

    val choreDate = MutableLiveData<String>()


    fun addChore(chore: Chore) {
        viewModelScope.launch {
            repository.insert(chore)
        }
    }

    sealed class ChoreViewModelEvents {

    }
}