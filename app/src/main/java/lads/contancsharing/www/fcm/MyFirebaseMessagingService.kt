package lads.contancsharing.www.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


import com.google.gson.Gson
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper
import lads.contancsharing.www.utils.MyNotification
import lads.contancsharing.www.utils.SessionManager


class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var sessionManager: SessionManager
    var vibrate = longArrayOf(0, 0, 0, 0)

    //    lateinit var sound: Uri
    var TAG = "com.lads.contactsharing"

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    override fun onNewToken(token: String) {

        sessionManager = SessionManager.getInstance(applicationContext)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        if (sessionManager.user != null) {

          Helper.sessionRefresh()
        }
    }

    override fun onCreate() {
        super.onCreate()

    }

    //    {type_id=1, body=Your Order #2103261 has been Delivered., type=Order, sound=default, title=Order Status Updated}
    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        val params = remoteMessage.data

        // Check if message contains a data payload.
        // {type_id=23, body=Your Order #21040823 has been Accepted., type=Order, sound=default, title=Order Status Updated}
        if (remoteMessage.data.isNotEmpty()) {

            remoteMessage.data.let { paramData ->

                val title: String = paramData["title"].toString()
                val message: String = paramData["body"].toString()
                var type: String = ""
                var typeId: Int = 0

                if (paramData.containsKey("type")) {
                    type = paramData["type"].toString()
                }
                if (paramData.containsKey("type_id")) {
                    typeId = paramData["type_id"].toString().toInt()
                }

                var notification = MyNotification().apply {
                    this.title = title
                    this.message = message
                    this.type = type
                    this.typeId = typeId
                }

                sendNotification(title, message, Gson().toJson(notification))
            }
        }

        // sendNotification("Test notification", "Test message", "Test data")
        // Check if message contains a notification payload.
//        if (remoteMessage.notification != null) {
//            Debugger.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.title)
//        }

        // sendNotification("Abyat Notification", "Abyat Notification Test", "Abyat Notification Test")
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param //messageBody FCM message body received.
     */
    private fun sendNotification(title: String, message: String?, data: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(AppConstant.KEY_NOTIFICATION_DATA, data)
        val pendingIntent = getActivity(this, 0 /* Request code */, intent, FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        //var sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.notification_tone)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title).setContentText(message).setVibrate(vibrate)
                //            .setSound(sound, AudioManager.STREAM_NOTIFICATION)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            // Creating an Audio Attribute
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            //          channel.setVibrationPattern(LongArray(0))
            channel.enableVibration(false)
            channel.enableLights(true)
            channel.description = getString(R.string.app_name)
//            channel.setSound(sound, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        var notification = notificationBuilder.build()
        notificationManager.notify(
            AppConstant.NOTIFICATION_ID /* ID of notification */,
            notification
        )
    }
}
