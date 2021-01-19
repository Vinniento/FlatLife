package fh.wfp2.flatlife.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val approved: Boolean,
    val flatId: Long

) {
    override fun toString(): String = name
}



