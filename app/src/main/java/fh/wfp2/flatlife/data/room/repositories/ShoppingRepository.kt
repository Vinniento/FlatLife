package fh.wfp2.flatlife.data.room.repositories


import fh.wfp2.flatlife.data.room.ShoppingDao
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber


class ShoppingRepository(private val shoppingDao: ShoppingDao) :
    AbstractRepository<ShoppingItem>(shoppingDao) {
    val shoppingRepositoryJob = Job()

    private val ioScope = CoroutineScope(shoppingRepositoryJob + Dispatchers.IO)

    fun getAllItems(): Flow<List<ShoppingItem>> {
        Timber.i("All items retrieved")
        return shoppingDao.getItemsSortedByIsBought()
    }

    suspend fun deleteAllBoughtItems() {
        ioScope.launch {
            shoppingDao.deleteAllBoughtItems()
        }
    }
}

