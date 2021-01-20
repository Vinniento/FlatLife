package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task")
@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isImportant: Boolean = false,
    var isSynced: Boolean = false,
    var isDeletedLocally : Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "\n Task: $id \nName: $name \n isComplete: $isComplete \n createdAt: $createdDateFormatted  \n important: $isImportant"
    }

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)

}