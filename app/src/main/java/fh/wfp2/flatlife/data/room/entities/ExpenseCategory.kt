package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "expenseCategory")
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val categoryName: String,
    @Expose(deserialize = false, serialize = false)
    var isSynced: Boolean = false,
    @Expose(deserialize = false, serialize = false)
    var isDeletedLocally: Boolean = false
) : Parcelable