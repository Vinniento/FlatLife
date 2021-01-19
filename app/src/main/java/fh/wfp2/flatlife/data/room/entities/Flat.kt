package fh.wfp2.flatlife.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Flat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
    ) {
}