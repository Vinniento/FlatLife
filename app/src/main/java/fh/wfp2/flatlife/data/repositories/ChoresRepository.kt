package fh.wfp2.flatlife.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import fh.wfp2.flatlife.data.remote.ChoreApi
import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.room.daos.ChoresDao
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.other.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ChoresRepository @Inject constructor(
    private val choresDao: ChoresDao,
    private val choreApi: ChoreApi,
    private val context: Application
) :
    AbstractRepository<Chore>(choresDao) {

    private var curItemResponse: Response<List<Chore>>? = null


    private suspend fun insertItems(items: List<Chore>) {
        items.forEach { insertItem(it) }
    }

    suspend fun deleteItem(item: Chore): Boolean {

        val response = try {
            choreApi.deleteChore(DeleteItemRequest(item.id))
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            choresDao.delete(item)
            true;
        } else {
            choresDao.insert(item.apply { isDeletedLocally = true })
            true;
        }
    }

    suspend fun insertItem(item: Chore) {

        val response = try {
            choreApi.addChore(item)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            choresDao.insert(item.apply {
                isSynced = true; id = response.body()?.itemID!!
            })
        } else {
            choresDao.insert(item)
        }
    }

    private suspend fun insertChoresLocally(categories: List<Chore>) {
        categories.forEach { insert(it) }
    }

    private suspend fun syncItems() {
        Timber.i("he")
        val locallyDeleteTaskIDs = choresDao.getAllLocallyDeletedItemIDs()
        locallyDeleteTaskIDs.forEach { item -> deleteItem(item) }

        val unsyncedItems = choresDao.getAllUnsyncedItems()
        insertItems(unsyncedItems)
        curItemResponse = choreApi.getAllChores()

        curItemResponse?.body()?.let {
            choresDao.deleteAllItems()
            Timber.i("all items deleted")
        }
    }

    fun getAllChores(): Flow<Resource<List<Chore>>> {
        return networkBoundResource(
            query = {
                choresDao.getAllChores()
            },
            fetch = {
                syncItems()
                Timber.i("Items synchronized: ${curItemResponse?.body()?.size}")
                curItemResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertChoresLocally(it.onEach { item -> item.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }
}