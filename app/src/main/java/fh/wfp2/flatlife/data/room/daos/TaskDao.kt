package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Task
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

    @Query("SELECT * FROM task  where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant = 0, createdAt")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant = 0, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("DELETE FROM task where isComplete = 1")
    fun deleteAllCompletedTasks()

    @Query("SELECT * FROM TASK")
    fun getAllTasks(): Flow<List<Task>>


}