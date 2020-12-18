package fh.wfp2.flatlife.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import fh.wfp2.flatlife.data.ShoppingRepository
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.data.room.ShoppingItem
import kotlinx.coroutines.*
import timber.log.Timber


class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShoppingRepository

    private val shoppingViewModelJob = Job()
    private val uiScope = CoroutineScope(shoppingViewModelJob + Dispatchers.Main)

    private val _allItems = MutableLiveData<List<ShoppingItem>>()
    val allItems: LiveData<List<ShoppingItem>> = _allItems

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message.toString())
    }

    init {
        val shoppingDao = FlatLifeRoomDatabase.getInstance(application).shoppingDao()
        repository = ShoppingRepository(shoppingDao)
        viewModelScope.launch {
            repository.apply {
                insert(ShoppingItem(0, "Apples"))
                insert(ShoppingItem(0, "Bananaas"))
                insert(ShoppingItem(0, "Kitchen stuff"))
            }
        }
        Timber.i("Repository created in viewModel")
        viewModelScope.launch(errorHandler) {
            _allItems.postValue(repository.getAllItems().value)
        }
    }


    fun insert(item: ShoppingItem) {
        uiScope.launch(errorHandler) {

            Timber.i("Item added ${item.name}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        shoppingViewModelJob.cancel()
    }

}

class ShoppingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            Timber.i("Creating viewModel")
            return ShoppingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

