package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao : AbstractDao<ExpenseCategory> {
    @Query("SELECT * FROM expenseCategory")
    fun getAllItems(): Flow<List<ExpenseCategory>>

    @Query("SELECT categoryName FROM expenseCategory")
    fun getAllItemNames(): Flow<List<String>>

    @Query("SELECT * FROM expenseCategory where isDeletedLocally = 1")
    fun getAllLocallyDeletedItemIDs(): List<ExpenseCategory>

    @Query("select * from expenseCategory where isSynced = 0 AND isDeletedLocally = 0 ")
    suspend fun getAllUnsyncedItems(): List<ExpenseCategory>

    @Query("DELETE from expenseCategory")
    suspend fun deleteAllItems()
}