package lads.contancsharing.www.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lads.contancsharing.www.room.dao.NotificationDao
import lads.contancsharing.www.room.entities.ModelNotification

@Database(entities = [ModelNotification::class], version = DB_VERSION)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDataDao(): NotificationDao
    companion object {
        @Volatile
        private var databaseInstance: CartDatabase? = null

        fun getDatabaseInstance(mContext: Context): CartDatabase =
            databaseInstance ?: synchronized(this) {
                databaseInstance ?: buildDatabaseInstance(mContext).also {
                    databaseInstance = it
                }
            }

        private fun buildDatabaseInstance(mContext: Context) =
            Room.databaseBuilder(mContext, CartDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()

    }
}
const val DB_VERSION = 2
const val DB_NAME = "notifications.db"

