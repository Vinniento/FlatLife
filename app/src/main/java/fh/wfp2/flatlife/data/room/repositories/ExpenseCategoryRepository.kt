package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.ExpenseCategoryDao
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import kotlinx.coroutines.flow.Flow

class ExpenseCategoryRepository(private val expenseCategoryDao: ExpenseCategoryDao) :
    AbstractRepository<ExpenseCategory>(expenseCategoryDao) {
    fun getAllItems(): Flow<List<ExpenseCategory>> {
        return expenseCategoryDao.getAllItems()
    }fun getAllItemNames(): Flow<List<String>> {
        return expenseCategoryDao.getAllItemNames()
    }
}