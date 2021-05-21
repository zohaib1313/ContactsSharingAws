package lads.contancsharing.www.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import lads.contancsharing.www.room.entities.ModelNotification


@Dao
interface NotificationDao {


    @Query("SELECT * FROM ${ModelNotification.TABLE_NAME}")
    fun getAllCartItems(): LiveData<List<ModelNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(data: ModelNotification)


    @Query("DELETE FROM ${ModelNotification.TABLE_NAME} WHERE ${ModelNotification.NOTIFICATION_ID}=:id")
    suspend fun deleteNotification(id: String)

    @Query("UPDATE ${ModelNotification.TABLE_NAME} SET ${ModelNotification.STATUS} = :status WHERE ${ModelNotification.NOTIFICATION_ID} =:id")
    suspend fun update(id: String, status: String)

    @Query("DELETE FROM ${ModelNotification.TABLE_NAME}")
    suspend fun deleteTable()


    @Query("SELECT COUNT() FROM ${ModelNotification.TABLE_NAME} WHERE ${ModelNotification.NOTIFICATION_ID} =:id")
    suspend fun count(id: String): Int

}