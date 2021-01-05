package fh.wfp2.flatlife.data.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "expenseCategory")
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true)
    val categoryId : Long = 0,
    val categoryName: String
) : Parcelable