package fh.wfp2.flatlife.data.repositories

import fh.wfp2.flatlife.data.room.daos.ChoresDao
import fh.wfp2.flatlife.data.room.entities.Chore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChoresRepository @Inject constructor(private val choresDao: ChoresDao) :
    AbstractRepository<Chore>(choresDao) {

    fun getAllChores(): Flow<List<Chore>> = choresDao.getAllChores()
}