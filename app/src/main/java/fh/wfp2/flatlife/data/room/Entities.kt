package fh.wfp2.flatlife.data.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "users")
class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String,
    firstname: String,
    lastname: String,
    username: String,
    email: String,
    approved: Boolean
) {
    override fun toString(): String = name
}

@Entity(tableName = "task")
@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String?,
    val isComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val isImportant: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "\n Task: $id \nName: $name \n isComplete: $isComplete \n createdAt: $createdAt  \n important: $isImportant"
    }

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)

}

@Entity(tableName = "shopping_items")
@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
data class ShoppingItems(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String?,
    val isBought: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    override fun toString(): String {
        return "\n Shopping Item: $id \nName: $name \n isBought: $isBought \n createdAt: $createdDateFormatted  \n "
    }

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}



