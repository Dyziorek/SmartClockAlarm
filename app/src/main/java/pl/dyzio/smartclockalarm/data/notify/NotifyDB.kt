package pl.dyzio.smartclockalarm.data.notify

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifyDB {

    @Query(
  """
       SELECT * from my_notifications 
        order by date desc 
       LIMIT :limit 
       """
    )
    fun getAll(limit : Int) : Flow<List<NotifyItem>>

    @Query("SELECT * from my_notifications")
    fun getAllLocal() : List<NotifyItem>

    @Query("SELECT * from my_notifications where notifyId = :identity")
    fun getById(identity : Long) : NotifyItem?

    @Query("SELECT COUNT(*) from my_notifications")
    fun getCount() : Int

    @Insert
    fun insert(item: NotifyItem)

    @Update
    suspend fun update(item: NotifyItem)

    @Delete
    suspend fun delete(item: NotifyItem)

    @Query ("DELETE from my_notifications")
    suspend fun purgeTable()

    @Query( "DELETE from my_notifications where ROWID in (SELECT ROWID from my_notifications order by ROWID DESC limit -1 OFFSET 100 )")
    suspend fun trimTable()


}

