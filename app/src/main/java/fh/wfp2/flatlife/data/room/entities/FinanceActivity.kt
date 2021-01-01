package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Entity(tableName = "financeActivity")
@Parcelize //make it parcelable -> able to send the entire object to another fragmetn
data class FinanceActivity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,

) : Parcelable {
    override fun toString(): String {
        return "\n Task: $id \nName: $name \n"
    }

}
