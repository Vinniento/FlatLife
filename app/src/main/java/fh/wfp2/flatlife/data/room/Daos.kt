package fh.wfp2.flatlife.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fh.wfp2.flatlife.ui.viewmodels.SortOrder
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
    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todos: Todo)

    fun getTodos(
        searchQuery: String,
        hideCompleted: Boolean,
        sortOrder: SortOrder
    ): Flow<List<Todo>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTodosSortedByDateCreated(searchQuery, hideCompleted)
            SortOrder.BY_NAME -> getTodosSortedByName(searchQuery, hideCompleted)
        }


    @Query("SELECT * FROM todos  where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY  createdAt asc ")
    fun getTodosSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos where (isComplete != :hideCompleted OR isComplete = 0) AND name like '%' || :searchQuery || '%' ORDER BY  name asc")
    fun getTodosSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Todo>>

    @Query("SELECT * from todos order by id desc limit 1")
    fun getTodoWithHighestID(): Todo
}