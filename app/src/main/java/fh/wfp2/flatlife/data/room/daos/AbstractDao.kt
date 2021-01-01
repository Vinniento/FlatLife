package fh.wfp2.flatlife.data.room.daos

import androidx.room.*

@Dao
interface AbstractDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objectInstance: T)

    @Delete
    suspend fun delete(objectInstance: T)

    @Update
    suspend fun update(objectInstance: T)

}