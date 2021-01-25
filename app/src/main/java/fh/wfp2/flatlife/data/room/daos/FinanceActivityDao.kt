package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceActivityDao : AbstractDao<FinanceActivity> {

    @Query("SELECT * from financeactivity where isDeletedLocally = 0")
    fun getAllActivities(): Flow<List<FinanceActivity>>

    @Query("SELECT * FROM financeactivity where isDeletedLocally = 1")
    suspend fun getAllLocallyDeletedItemIDs(): List<FinanceActivity>

    @Query("select * from financeactivity where isSynced = 0 AND isDeletedLocally = 0 ")
    suspend fun getAllUnsyncedItems(): List<FinanceActivity>

    @Query("DELETE from financeactivity")
    suspend fun deleteAllItems()
}