package fh.wfp2.flatlife.data.room.entities.mapping

import androidx.room.Embedded
import androidx.room.Relation
import fh.wfp2.flatlife.data.room.entities.Flat
import fh.wfp2.flatlife.data.room.entities.User

data class FlatWithUsers(
    @Embedded val flat: Flat,
    @Relation(
        parentColumn = "id",
        entityColumn = "flatId"
    )
    val users: List<User>
) {
    override fun toString(): String {
        return "Flat id: ${flat.id} \n inhabitants: $users"
    }
}
