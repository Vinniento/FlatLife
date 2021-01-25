package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import fh.wfp2.flatlife.data.room.daos.AbstractDao
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao : AbstractDao<ShoppingItem> {

    @Query("SELECT * FROM shopping_items where isDeletedLocally = 0 ORDER BY isBought, createdAt")
    fun getItemsSortedByIsBought(): Flow<List<ShoppingItem>>

    @Query("DELETE FROM shopping_items where isBought = 1 ")
    fun deleteAllBoughtItems()

    @Query("SELECT * FROM SHOPPING_ITEMS WHERE isBought = 1")
    fun getAllBoughtItems(): List<ShoppingItem>

    @Query("select * from shopping_items where isSynced = 0 AND isDeletedLocally = 0 ")
    suspend fun getAllUnsyncedItems(): List<ShoppingItem>

    @Query("select * from shopping_items where isDeletedLocally = 1 ")
    suspend fun getAllLocallyDeletedItemIDs(): List<ShoppingItem>

    @Query("DELETE from shopping_items")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM shopping_items where isDeletedLocally = 0")
    fun getAllItems(): Flow<List<ShoppingItem>>
}