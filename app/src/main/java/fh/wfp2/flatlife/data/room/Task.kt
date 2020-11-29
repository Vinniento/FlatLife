package fh.wfp2.flatlife.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

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