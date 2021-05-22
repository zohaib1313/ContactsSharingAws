package lads.contancsharing.www.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.utils.AppConstant

class CustomNotification {

    var contxt: Context

    constructor(contxt: Context) {
        this.contxt = contxt
    }


    fun CustomNotification() {
        val remoteViews = RemoteViews(contxt.applicationContext.packageName, R.layout.mynotification)
        //remoteViews.setTextViewText(R.id.titleNoti, "title")
        //remoteViews.setImageViewResource(R.id.bodyNoti, R.drawable.app_icon)

        Log.d("taaag","notificatin method")
        val intent = Intent(contxt, MainActivity::class.java)
        intent.putExtra("title", "strtitle")
        intent.putExtra("text", "strtext")
        val pIntent: PendingIntent =
            PendingIntent.getActivity(contxt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(contxt.applicationContext,contxt.getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        // Create Notification Manager
        val notificationManager =
            contxt.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Build Notification with Notification Manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "my_channel_01"
            val name: CharSequence = "my_channel"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(mChannel)
        }


        notificationManager.notify(AppConstant.NOTIFICATION_ID, builder.build())
        // notificationmanager.notify(0, builder.build())
    }

}