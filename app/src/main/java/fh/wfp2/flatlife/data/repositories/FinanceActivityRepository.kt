package fh.wfp2.flatlife.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import fh.wfp2.flatlife.data.remote.FinanceApi
import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.room.daos.FinanceActivityDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.other.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class FinanceActivityRepository @Inject constructor(
    private val financeActivityDao: FinanceActivityDao,
    private val financeApi: FinanceApi,
    private val context: Application
) :
    AbstractRepository<FinanceActivity>(financeActivityDao) {

    private var curItemResponse: Response<List<FinanceActivity>>? = null



    private suspend fun insertItems(items: List<FinanceActivity>) {
        items.forEach { insertItem(it) }
    }

    suspend fun deleteItem(item: FinanceActivity): Boolean {

        val response = try {
            financeApi.deleteItem(DeleteItemRequest(item.id))
        } catch (e: Exception) {
            null
        }
        return if (response != null && response.isSuccessful) {
            financeActivityDao.delete(item)
            true;
        } else {
            financeActivityDao.insert(item.apply { isDeletedLocally = true })
            true;
        }
    }

    suspend fun insertItem(item: FinanceActivity) {

        val response = try {
            financeApi.addItem(item)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            financeActivityDao.insert(item.apply {
                isSynced = true; id = response.body()?.itemID!!
            })
        } else {
            financeActivityDao.insert(item)
        }
    }

    private suspend fun insertActivitiesLocally(categories: List<FinanceActivity>) {
        categories.forEach { insert(it) }
    }

    private suspend fun syncItems() {
        Timber.i("he")
        val locallyDeleteTaskIDs = financeActivityDao.getAllLocallyDeletedItemIDs()
        locallyDeleteTaskIDs.forEach { item -> deleteItem(item) }

        val unsyncedItems = financeActivityDao.getAllUnsyncedItems()
        insertItems(unsyncedItems)
        curItemResponse = financeApi.getAllActivities()

        curItemResponse?.body()?.let {
            financeActivityDao.deleteAllItems()
            Timber.i("all items deleted")
        }
    }

    fun getAllActivities(): Flow<Resource<List<FinanceActivity>>> {
        return networkBoundResource(
            query = {
                financeActivityDao.getAllActivities()
            },
            fetch = {
                syncItems()
                Timber.i("Items synchronized: ${curItemResponse?.body()?.size}")
                curItemResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertActivitiesLocally(it.onEach { item -> item.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }
}