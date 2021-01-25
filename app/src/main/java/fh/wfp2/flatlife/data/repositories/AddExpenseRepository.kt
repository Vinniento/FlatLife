package fh.wfp2.flatlife.data.repositories

import fh.wfp2.flatlife.data.room.daos.AddExpenseDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import javax.inject.Inject

class AddExpenseRepository @Inject constructor(
    private val addExpenseDao: AddExpenseDao) :
    AbstractRepository<FinanceActivity>(addExpenseDao) {
}