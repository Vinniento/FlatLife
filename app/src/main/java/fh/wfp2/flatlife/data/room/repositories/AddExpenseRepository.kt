package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.AddExpenseDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity

class AddExpenseRepository(private val addExpenseDao: AddExpenseDao) :
    AbstractRepository<FinanceActivity>(addExpenseDao) {
}