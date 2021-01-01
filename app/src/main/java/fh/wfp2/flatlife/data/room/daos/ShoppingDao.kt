package fh.wfp2.flatlife.data.room

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.daos.AbstractDao
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao : AbstractDao<ShoppingItem> {

    @Query("SELECT * FROM shopping_items ORDER BY isBought, createdAt")
    fun getItemsSortedByIsBought(): Flow<List<ShoppingItem>>

    @Query("DELETE FROM shopping_items where isBought = 1")
    fun deleteAllBoughtItems()
}