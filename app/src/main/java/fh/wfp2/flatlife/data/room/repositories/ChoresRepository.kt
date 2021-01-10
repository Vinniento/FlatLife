package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.ChoresDao
import fh.wfp2.flatlife.data.room.entities.Chore
import kotlinx.coroutines.flow.Flow

class ChoresRepository(private val choresDao: ChoresDao) :
    AbstractRepository<Chore>(choresDao) {

     fun getAllChores(): Flow<List<Chore>> = choresDao.getAllChores()
}