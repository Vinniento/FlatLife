package fh.wfp2.flatlife.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.other.checkForInternetConnection
import fh.wfp2.flatlife.data.remote.FinanceApi
import fh.wfp2.flatlife.data.room.daos.ExpenseCategoryDao
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.other.Resource
import fh.wfp2.flatlife.other.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ExpenseCategoryRepository @Inject constructor(
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val financeApi: FinanceApi,
    private val context: Application

) :
    AbstractRepository<ExpenseCategory>(expenseCategoryDao) {

    private var curItemResponse: Response<List<ExpenseCategory>>? = null


    private suspend fun syncItems() {
        val unsyncedItems = expenseCategoryDao.getAllUnsyncedItems()
        insertItems(unsyncedItems)
        curItemResponse = financeApi.getAllExpenseCategories()

        curItemResponse?.body()?.let {
            expenseCategoryDao.deleteAllItems()
            Timber.i("all items deleted")
        }
    }


    private suspend fun insertItems(items: List<ExpenseCategory>) {
        items.forEach { insertItem(it) }
    }

    suspend fun insertItem(item: ExpenseCategory) {

        val response = try {
            financeApi.addCategory(item)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            expenseCategoryDao.insert(item.apply {
                isSynced = true; id = response.body()?.itemID!!
            })
        } else {
            expenseCategoryDao.insert(item)
        }
    }

    private suspend fun insertCategoriesLocally(categories: List<ExpenseCategory>) {
        categories.forEach { insert(it) }
    }

    fun getAllItems(): Flow<Resource<List<ExpenseCategory>>> {
        return networkBoundResource(
            query = {
                expenseCategoryDao.getAllItems()
            },
            fetch = {
                syncItems()
                Timber.i("Items synchronized: ${curItemResponse?.body()?.size}")
                curItemResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertCategoriesLocally(it.onEach { item -> item.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    fun getAllItemNames(): Flow<List<String>> {
        return expenseCategoryDao.getAllItemNames()
    }
}