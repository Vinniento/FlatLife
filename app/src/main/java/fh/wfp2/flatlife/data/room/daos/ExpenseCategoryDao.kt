package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao : AbstractDao<ExpenseCategory> {
    @Query("SELECT * FROM expenseCategory")
    fun getAllItems(): Flow<List<ExpenseCategory>>
}