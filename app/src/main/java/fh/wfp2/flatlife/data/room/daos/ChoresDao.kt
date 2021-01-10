package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.entities.Chore
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoresDao : AbstractDao<Chore> {

    @Query("SELECT * FROM chore")
    fun getAllChores(): Flow<List<Chore>>
}