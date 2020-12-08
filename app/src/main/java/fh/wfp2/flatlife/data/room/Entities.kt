package fh.wfp2.flatlife.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(
    @PrimaryKey(autoGenerate = true) val userId: Long,
    val name: String,
    firstname: String,
    lastname: String,
    username: String,
    email: String,
    approved: Boolean
) {
    override fun toString(): String = name
}

@Entity(tableName = "todos")
class Todo(
    @PrimaryKey(autoGenerate = true) val todoId: Long,
    val name: String,
    val isComplete: Boolean,
    val createdAt: String,
    val createdBy: String
) {
    //isDone() usw auch hier hinein?
    override fun toString(): String = name
}

@Entity(tableName = "tasks")
class Task(
    @PrimaryKey(autoGenerate = true) val taskid: Long,
    val name: String,
    val createdAt: String,
    val dueBy: String,
    val createdBy: String
) {
    //isDone() usw auch hier hinein?
    override fun toString(): String = name
}


