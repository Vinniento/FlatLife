package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.FinanceActivityDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.entities.mapping.ExpenseCategoryWithFinanceActivity
import kotlinx.coroutines.flow.Flow

class FinanceActivityRepository(private val financeActivityDao: FinanceActivityDao) :
    AbstractRepository<FinanceActivity>(financeActivityDao) {

     fun getAllActivities(): Flow<List<FinanceActivity>> {
        return financeActivityDao.getAllActivities()
    }

}