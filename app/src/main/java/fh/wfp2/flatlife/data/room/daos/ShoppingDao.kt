import androidx.lifecycle.LiveData
import androidx.room.*
import fh.wfp2.flatlife.data.room.ShoppingItem

@Dao
interface ShoppingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingItem: ShoppingItem)

    @Delete
    suspend fun delete(shoppingItem: ShoppingItem)

    @Update
    suspend fun update(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_items ORDER BY isBought, createdAt")
    fun getItemsSortedByIsBought(): LiveData<List<ShoppingItem>>

    @Query("DELETE FROM shopping_items where isBought = 1")
    fun deleteAllBoughtItems()
}