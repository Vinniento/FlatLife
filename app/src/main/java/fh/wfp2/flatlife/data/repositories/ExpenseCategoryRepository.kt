package fh.wfp2.flatlife.data.repositories

import fh.wfp2.flatlife.data.room.daos.ExpenseCategoryDao
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseCategoryRepository @Inject constructor(private val expenseCategoryDao: ExpenseCategoryDao) :
    AbstractRepository<ExpenseCategory>(expenseCategoryDao) {
    fun getAllItems(): Flow<List<ExpenseCategory>> {
        return expenseCategoryDao.getAllItems()
    }

    fun getAllItemNames(): Flow<List<String>> {
        return expenseCategoryDao.getAllItemNames()
    }
}