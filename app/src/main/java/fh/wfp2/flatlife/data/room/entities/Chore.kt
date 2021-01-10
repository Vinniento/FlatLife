package fh.wfp2.flatlife.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chore(
    @PrimaryKey(autoGenerate = true)
    val choreId: Long = 0,
    val name: String,
    val dueBy: String, //todo formatting -> raco datepicker und format
    val isComplete: Boolean = false,
    val assignedTo : String = "Vince" //todo n:m chore : user
) {
    override fun toString(): String {
        return "id: $choreId, name: $name, due by: $dueBy, isComplete: $isComplete, assignedTo: $assignedTo"
    }
}
