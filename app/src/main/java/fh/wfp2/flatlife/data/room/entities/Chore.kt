package fh.wfp2.flatlife.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity
data class Chore(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val effort: Int,
    val dueBy: String, //todo formatting -> raco datepicker und format
    val isComplete: Boolean = false,
    val assignedTo: String,
    @Expose(deserialize = false, serialize = false)
    var isSynced: Boolean = false,

    @Expose(deserialize = false, serialize = false)
    var isDeletedLocally: Boolean = false
) {
    override fun toString(): String {
        return "id: $id, name: $name, due by: $dueBy, isComplete: $isComplete, assignedTo: $assignedTo"
    }
}
