package lads.contancsharing.www.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = ModelNotification.TABLE_NAME)
class ModelNotification(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = NOTIFICATION_ID)
    var notificationId: String = 0.toString(),

    @ColumnInfo(name = FILE_PATH_URL)
    var filePath: String = "",


    @ColumnInfo(name = MESSAGE)
    var message: String = "",
    @ColumnInfo(name = FROM_USER_TOKEN)
    var fromUserToken: String = "",
    @ColumnInfo(name = FROM_USER_ID)
    var fromUserId: String = "",
    @ColumnInfo(name = FROM_USER_IMAGE)
    var fromUserImage: String = "",
    @ColumnInfo(name = FROM_USER_NAME)
    var fromUserName: String = "",

    @ColumnInfo(name = FROM_USER_NUMBER)
    var fromUserNumber: String = "",

    @ColumnInfo(name = STATUS)
    var staus: String = "",

    ) {
    companion object {
        const val TABLE_NAME = "notificationTable"
        const val NOTIFICATION_ID = "id"
        const val FILE_PATH_URL = "filePath"
        const val MESSAGE = "message"
        const val FROM_USER_TOKEN = "formUserToken"
        const val FROM_USER_ID = "formUserId"
        const val FROM_USER_IMAGE = "fromUserImage"
        const val FROM_USER_NAME = "fromUserName"
        const val FROM_USER_NUMBER = "fromUserNumber"
        const val STATUS = "status"


    }


    interface JSONConvertable {
        fun toJSON(): String = Gson().toJson(this)
    }

    inline fun <reified T : JSONConvertable> String.toObject(): T =
        Gson().fromJson(this, T::class.java)


//    val paramObject = JSONObject()
//    paramObject.put("token", shareWith.user?.deviceToken)
//    paramObject.put("title", userObj.phone)
//    paramObject.put("message", "share you a file.\nDo you want to accept?")
//    paramObject.put("url", filePath)
//    paramObject.put("id", shareWith.id)


}