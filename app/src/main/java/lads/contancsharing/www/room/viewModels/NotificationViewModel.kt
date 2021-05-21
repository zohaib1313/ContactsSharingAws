package lads.contancsharing.www.room.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import lads.contancsharing.www.room.AppRepository
import lads.contancsharing.www.room.database.CartDatabase
import lads.contancsharing.www.room.entities.ModelNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository
    val allCartItemsInViewModel: LiveData<List<ModelNotification>>

    init {
        val cartDao = CartDatabase.getDatabaseInstance(application.applicationContext).cartDataDao()
        repository = AppRepository(cartDao)
        allCartItemsInViewModel = repository.allCartItems
    }


    fun insert(modelNotification: ModelNotification) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(modelNotification)
    }

    fun delete(modelNotification: ModelNotification) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(modelNotification)
    }

    fun deleteTable() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTable()
    }


    fun update(id: String, status: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(id, status)
    }

    fun count(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.count(id)
    }

}