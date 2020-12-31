package fh.wfp2.flatlife.data.room.entities

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



