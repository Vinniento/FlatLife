package fh.wfp2.flatlife.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val createdAt: String,
    val dueBy: String,
    val createdBy: String
)