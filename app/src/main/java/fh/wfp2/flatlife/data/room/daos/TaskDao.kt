package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.entities.Task
import kotlinx.coroutines.flow.Flow


//TODO ein abstraktes dao erstellen welches die grundfunktionen hat und die anderen implementieren das dann

@Dao
interface TaskDao : AbstractDao<Task> {
    fun getTasks(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(searchQuery, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
        }

    @Query("SELECT * FROM task  where (isComplete != :hideCompleted OR isComplete = 0) AND isDeletedLocally = 0 AND name like '%' || :searchQuery || '%' ORDER BY isImportant = 0, createdAt")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task where (isComplete != :hideCompleted OR isComplete = 0) AND isDeletedLocally = 0 AND name like '%' || :searchQuery || '%' ORDER BY isImportant = 0, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("DELETE FROM task where isComplete = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("select * from TASK where isSynced = 0 AND isDeletedLocally = 0 " )
    suspend fun getAllUnsyncedTasks(): List<Task>

    @Query("select * from TASK where isDeletedLocally = 1 " )
    suspend fun getAllLocallyDeletedTasks(): List<Task>

    @Query("SELECT * FROM TASK where isDeletedLocally = 0")
    fun getAllTasks(): Flow<List<Task>>

    @Query("DELETE from TASK")
    suspend fun deleteAllTasks()

    @Query("SELECT * from task where isDeletedLocally = 1")
    suspend fun getLocallyDeletedTaskIDs(): List<Task>

    @Query("SELECT * FROM TASK WHERE isComplete = 1")
    suspend fun getAllCompletedTasks() : List<Task>

}