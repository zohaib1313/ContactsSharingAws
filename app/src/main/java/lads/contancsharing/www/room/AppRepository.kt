package lads.contancsharing.www.room

import androidx.lifecycle.LiveData
import lads.contancsharing.www.room.dao.NotificationDao
import lads.contancsharing.www.room.entities.ModelNotification

class AppRepository(private val notificationDao: NotificationDao) {

    val allCartItems: LiveData<List<ModelNotification>> = notificationDao.getAllCartItems()


    suspend fun insert(modelNotification: ModelNotification) {
        notificationDao.insertNotification(modelNotification)
    }

    suspend fun update(id: String, status: String) {
        notificationDao.update(id, status)
    }

    suspend fun delete(modelNotification: ModelNotification) {
        notificationDao.deleteNotification(modelNotification.notificationId)
    }

    suspend fun deleteTable() {
        notificationDao.deleteTable()
    }

    suspend fun count(id: String) {
        notificationDao.count(id)
    }
}