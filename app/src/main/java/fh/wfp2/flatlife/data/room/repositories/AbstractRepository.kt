package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.AbstractDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class AbstractRepository<T>(
    private val dao: AbstractDao<T>
) {

    private val job = Job()
    private val ioScope = CoroutineScope(job + Dispatchers.IO)

    suspend fun insert(item: T) {
        ioScope.launch {
            dao.insert(item)
        }
    }

    suspend fun update(item: T) {
        ioScope.launch {
            dao.update(
                item
            )
        }
    }

    suspend fun delete(item: T) {
        ioScope.launch {
            dao.delete(item)
        }
    }
}