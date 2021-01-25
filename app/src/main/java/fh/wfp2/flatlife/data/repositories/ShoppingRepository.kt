package fh.wfp2.flatlife.data.repositories


import android.app.Application
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import fh.wfp2.flatlife.data.remote.ShoppingApi
import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.room.daos.ShoppingDao
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.other.networkBoundResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val shoppingApi: ShoppingApi,
    private val context: Application
) :
    AbstractRepository<ShoppingItem>(shoppingDao) {
    private val shoppingRepositoryJob = Job()

    private val ioScope = CoroutineScope(shoppingRepositoryJob + Dispatchers.IO)

    suspend fun insertItem(shoppingItem: ShoppingItem) {

        val response = try {
            shoppingApi.addItem(shoppingItem)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            shoppingDao.insert(shoppingItem.apply {
                isSynced = true; id = response.body()?.itemID!!
            })
        } else {
            shoppingDao.insert(shoppingItem)
        }
    }

    private suspend fun insertItems(items: List<ShoppingItem>) {
        items.forEach { insertItem(it) }
    }

    suspend fun deleteItem(item: ShoppingItem): Boolean {

        val response = try {
            shoppingApi.deleteItem(DeleteItemRequest(item.id))
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            shoppingDao.delete(item)
            true;
        } else {
            shoppingDao.insert(item.apply { isDeletedLocally = true })
            true;
        }
    }

    private var curItemResponse: Response<List<ShoppingItem>>? = null

    private suspend fun syncItems() {
        val locallyDeleteTaskIDs = shoppingDao.getAllLocallyDeletedItemIDs()
        locallyDeleteTaskIDs.forEach { item -> deleteItem(item) }

        val unsyncedItems = shoppingDao.getAllUnsyncedItems()
        insertItems(unsyncedItems)
        curItemResponse = shoppingApi.getAllItems()

        curItemResponse?.body()?.let {
            shoppingDao.deleteAllItems()
            Timber.i("all items deleted")
        }
    }

    fun getAllItems(): Flow<List<ShoppingItem>> = shoppingDao.getAllItems()

    fun getAllShoppingItems(): Flow<Resource<List<ShoppingItem>>> {
        return networkBoundResource(
            query = {
                shoppingDao.getItemsSortedByIsBought()
            },
            fetch = {
                syncItems()
                Timber.i("Items synchronized: ${curItemResponse?.body()?.size}")
                curItemResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertItemsLocal(it.onEach { item -> item.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    private suspend fun insertItemsLocal(list: List<ShoppingItem>) {
        list.forEach { item -> insert(item) }
    }


    suspend fun deleteAllBoughtItems() {
        ioScope.launch {
            val response = try {
                shoppingApi.deleteAllBoughtItems()
            } catch (e: Exception) {
                null
            }
            if (response != null && response.isSuccessful) {
                shoppingDao.deleteAllBoughtItems()
            } else {
                val deletedItems = shoppingDao.getAllBoughtItems()
                insertItemsLocal(deletedItems.onEach { it.isDeletedLocally = true })
            }
        }
    }

    suspend fun updateItem(item: ShoppingItem) {
        val response = try {
            shoppingApi.addItem(item)
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            shoppingDao.update(item)
        } else {
            shoppingDao.insert(item.apply { !isBought; isSynced = false })
        }
    }
}