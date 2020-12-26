package fh.wfp2.flatlife.data


import androidx.lifecycle.LiveData
import fh.wfp2.flatlife.data.room.ShoppingDao
import fh.wfp2.flatlife.data.room.ShoppingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber


class ShoppingRepository(private val shoppingDao: ShoppingDao) {
    val shoppingRepositoryJob = Job()

    private val ioScope = CoroutineScope(shoppingRepositoryJob + Dispatchers.IO)

    suspend fun insert(shoppingItem: ShoppingItem) {
        ioScope.launch {
            shoppingDao.insert(shoppingItem)
            Timber.i("${shoppingItem.name} added")
        }
    }

    fun getAllItems(): Flow<List<ShoppingItem>> {
        Timber.i("All items retrieved")
        return shoppingDao.getItemsSortedByIsBought()
    }

    suspend fun update(shoppingItem: ShoppingItem) {
        ioScope.launch {
            shoppingDao.update(shoppingItem.copy(isBought = shoppingItem.isBought))
        }
    }

    suspend fun deleteItem(shoppingItem: ShoppingItem) {
        ioScope.launch {

            shoppingDao.delete(shoppingItem)
        }
    }

    suspend fun deleteAllBoughtItems() {
        ioScope.launch {
            shoppingDao.deleteAllBoughtItems()
        }
    }
}

