package fh.wfp2.flatlife.data.repositories

import fh.wfp2.flatlife.data.room.daos.FinanceActivityDao
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FinanceActivityRepository @Inject constructor(private val financeActivityDao: FinanceActivityDao) :
    AbstractRepository<FinanceActivity>(financeActivityDao) {

    fun getAllActivities(): Flow<List<FinanceActivity>> {
        return financeActivityDao.getAllActivities()
    }

}