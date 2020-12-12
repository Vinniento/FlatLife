package fh.wfp2.flatlife.data.room

import androidx.room.*
import fh.wfp2.flatlife.data.preferences.SortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * from tasks order by id desc limit 1")
    fun getHighestID(): Task
}

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    fun getTodos(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Todo>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTodosSortedByDateCreated(searchQuery, hideCompleted)
            SortOrder.BY_NAME -> getTodosSortedByName(searchQuery, hideCompleted)
        }


    @Query("SELECT * FROM todos  where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant, createdAt")
    fun getTodosSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY isImportant, name")
    fun getTodosSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * from todos order by id desc limit 1")
    fun getTodoWithHighestID(): Todo

    @Query("DELETE FROM todos where isComplete = 1")
    fun deleteAllCompletedTodos()
}