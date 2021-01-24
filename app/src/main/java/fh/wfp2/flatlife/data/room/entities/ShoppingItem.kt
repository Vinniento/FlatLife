package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Entity(tableName = "shopping_items")
@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name: String,
    val isBought: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    @Expose(deserialize = false, serialize = false)
    var isSynced: Boolean = false,
    @Expose(deserialize = false, serialize = false)
    var isDeletedLocally: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "\n Shopping Item: $id \nName: $name \n isBought: $isBought \n createdAt: $createdDateFormatted  \n "
    }

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}



