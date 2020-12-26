package fh.wfp2.flatlife.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import fh.wfp2.flatlife.data.preferences.SortOrder
import kotlinx.coroutines.flow.Flow


//TODO ein abstraktes dao erstellen welches die grundfunktionen hat und die anderen implementieren das dann

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    fun getTasks(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(searchQuery, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
        }


    @Query("SELECT * FROM task  where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant, createdAt")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("DELETE FROM task where isComplete = 1")
    fun deleteAllCompletedTasks()

    @Query("SELECT * FROM task where id == :taskId")
    fun getTaskById(taskId: Long): LiveData<Task>
}

