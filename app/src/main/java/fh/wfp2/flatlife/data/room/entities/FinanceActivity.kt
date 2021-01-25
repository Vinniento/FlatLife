package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
@Entity
data class FinanceActivity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val description: String,
    val categoryName: String,
    val price: String,
    val createdAt: Long = System.currentTimeMillis(),
    @Expose(deserialize = false, serialize = false)
    var isSynced: Boolean = false,
    @Expose(deserialize = false, serialize = false)
    var isDeletedLocally: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "\n Task: $id \nName: $description \n category: ${categoryName}"
    }

    val createdDateFormatted: String
        get() = when (createdAt) {
            //TODO einbauen wenn es derselbe tag ist, nur uhrzeit usw
            else -> DateFormat.getDateTimeInstance().format(createdAt)
        }
}