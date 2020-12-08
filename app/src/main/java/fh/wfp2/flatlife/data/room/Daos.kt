package fh.wfp2.flatlife.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * from tasks order by taskid desc limit 1")
    fun getHighestID(): Task
}

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("SELECT * FROM todos")
    fun getAllTodos(): List<Todo>

    @Query("SELECT * from todos order by todoId desc limit 1")
    fun getTodoWithHighestID(): Todo
}