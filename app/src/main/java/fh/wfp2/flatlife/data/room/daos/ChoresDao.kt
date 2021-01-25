package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.entities.Chore
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoresDao : AbstractDao<Chore> {

    @Query("SELECT * FROM chore")
    fun getAllChores(): Flow<List<Chore>>

    @Query("SELECT * FROM chore where isDeletedLocally = 1")
    suspend fun getAllLocallyDeletedItemIDs(): List<Chore>

    @Query("select * from chore where isSynced = 0 AND isDeletedLocally = 0 ")
    suspend fun getAllUnsyncedItems(): List<Chore>

    @Query("DELETE FROM CHORE")
    suspend fun deleteAllItems()
}