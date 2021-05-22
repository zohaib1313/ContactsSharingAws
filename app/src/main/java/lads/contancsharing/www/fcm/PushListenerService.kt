package lads.contancsharing.www.fcm

/*import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager*/

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import lads.contancsharing.www.R
import lads.contancsharing.www.activities.MainActivity
import lads.contancsharing.www.activities.MainActivity.Companion.getPinpointManager

import lads.contancsharing.www.utils.AppConstant
import lads.contancsharing.www.utils.Helper


class PushListenerService : FirebaseMessagingService() {

    private val TAG = "com.lads.contactsharing"

    // Intent action used in local broadcast
    private val ACTION_PUSH_NOTIFICATION = "push-notification"

    // Intent keys
    private val INTENT_SNS_NOTIFICATION_FROM = "from"
    private val INTENT_SNS_NOTIFICATION_DATA = "data"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Registering push notifications token: $token")

        Helper.refreshFcmToken(applicationContext)
        getPinpointManager(applicationContext)?.notificationClient?.registerDeviceToken(token)


        //RegistrationExample().registerWithSNS(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message: " + remoteMessage.data)
        Log.d(TAG, "Message: " + remoteMessage.from)
//        val notificationClient: NotificationClient = getPinpointManager(applicationContext)!!.notificationClient
//
//        val notificationDetails = NotificationDetails.builder()
//            .from(remoteMessage.from)
//            .mapData(remoteMessage.data)
//            .intentAction(NotificationClient.FCM_INTENT_ACTION)
//            .build()

        val dataMap1 = HashMap(remoteMessage.data)
        //sendNotification(dataMap1)
        //sendNotificationUpdated(remoteMessage.data, remoteMessage.notification)
        //sendNotifications(remoteMessage.data, remoteMessage.notification)
        CustomNotification(applicationContext).CustomNotification()

        //sendNotificationUpdated(dataMap1,remoteMessage.notification)
        //val dataMap = HashMap(remoteMessage.data)
        //broadcast(remoteMessage.from, dataMap)

        //val pushResult = notificationClient.handleCampaignPush(notificationDetails)
        // notifications channel creation

        //if (NotificationClient.CampaignPushResult.NOT_HANDLED != pushResult) {
        /**
         * The push message was due to a Pinpoint campaign.
         * If the app was in the background, a local notification was added
         * in the notification center. If the app was in the foreground, an
         * event was recorded indicating the app was in the foreground,
         * for the demo, we will broadcast the notification to let the main
         * activity display it in a dialog.
         */
        //if (NotificationClient.CampaignPushResult.APP_IN_FOREGROUND == pushResult) {
        /* Create a message that will display the raw data of the campaign push in a dialog. */
        //val dataMap = HashMap(remoteMessage.data)
        //broadcast(remoteMessage.from, dataMap)
        //}
        // return
        // }
    }

    private fun broadcast(from: String?, dataMap: HashMap<String, String>) {
        val intent = Intent(ACTION_PUSH_NOTIFICATION)
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from)
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendNotification(/*messageBody: String*/dataMap: HashMap<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0  /*Request code*/, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        /* val accept = Intent(this, HomeActivity::class.java)
         accept.action = "accept"
         val acceptBundle = Bundle()
         acceptBundle.putInt("accept", 1) //This is the value I want to pass

         accept.putExtras(acceptBundle)

         val pendingIntent = PendingIntent.getActivity(
             this, 0 , accept,
                     PendingIntent.FLAG_ONE_SHOT
         )*/

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(dataMap["pinpoint.notification.title"])
            .setContentText(dataMap["pinpoint.notification.body"])
            .setAutoCancel(false)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        val pendingIntentAccept = PendingIntent.getBroadcast(
            this,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.addAction(R.drawable.selected, "Yes", pendingIntentAccept)
        notificationBuilder.addAction(R.drawable.not_selected, "No", pendingIntentAccept)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    /**
     * Helper method to extract push message from bundle.
     *
     * @param data bundle
     * @return message string from push notification
     */
    fun getMessage(data: Bundle): String {
        return (data.get("data") as HashMap<*, *>).toString()
    }

    private fun sendNotificationUpdated(
        data: MutableMap<String, String>,
        notification: RemoteMessage.Notification?
    ) {

        val url = data["pinpoint.url"]
        val title: String = data["pinpoint.notification.title"].toString()//notification?.title!!
        val messageBody: String = data["pinpoint.notification.body"].toString()//notification.body!!
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("DEEP_LINK_URL", url)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationView = RemoteViews(
            packageName,
            R.layout.mynotification
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setCustomContentView(notificationView)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val pendingIntentAccept = PendingIntent.getBroadcast(
            this,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.addAction(
            R.drawable.ic_launcher_foreground,
            "Accept",
            pendingIntentAccept
        )
        notificationBuilder.addAction(R.drawable.icon_profile, "Reject", pendingIntentAccept)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun sendNotifications(
        data: MutableMap<String, String>,
        notification: RemoteMessage.Notification?
    ) {

        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.mynotification)
        //remoteViews.setTextViewText(R.id.titleNoti, "title")
        //remoteViews.setImageViewResource(R.id.bodyNoti, R.drawable.app_icon)
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("title", "strtitle")
        intent.putExtra("text", "strtext")
        val pIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,applicationContext.getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
            .setContent(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        // Create Notification Manager
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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








//
//
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val url = data["pinpoint.url"]
//        Log.d("Here", url.toString())
//        val title: String = data["pinpoint.notification.title"].toString()//notification?.title!!
//        val messageBody: String = data["pinpoint.notification.body"].toString()//notification.body!!
//        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("DEEP_LINK_URL", url)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val notificationView = RemoteViews(
//            packageName,
//            R.layout.mynotification
//        )
//
////        notificationView.setTextViewText(R.id., "$title $messageBody")
////        notificationView.setImageViewResource(R.id.notification_image, R.drawable.app_icon)
//
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.logo)
////            .setContentTitle(title)
////            .setContentText(messageBody)
//            .setAutoCancel(false)
//            .setSound(defaultSoundUri)
//            .setContent(notificationView)
//            .setContentIntent(pendingIntent)
//
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                getString(R.string.app_name),
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//
//            // Creating an Audio Attribute
//            val audioAttributes =
//                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
//            //          channel.setVibrationPattern(LongArray(0))
//            channel.enableVibration(false)
//            channel.enableLights(true)
//            channel.description = getString(R.string.app_name)
////            channel.setSound(sound, audioAttributes)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        //the intent that is started when the notification is clicked (works)
//
//
//        //this is the intent that is supposed to be called when the
//        //button is clicked
//
//        //this is the intent that is supposed to be called when the
//        //button is clicked
//        val acceptIntent = Intent(this, SwitchButtonListener::class.java)
//        acceptIntent.action = "accept:${url.toString()}"
//        acceptIntent.putExtra("DEEP_LINK_URL", url.toString())
//
//        val rejectIntent = Intent(this, SwitchButtonListener::class.java)
//        rejectIntent.action = "reject:${url.toString()}"
//        rejectIntent.putExtra("DEEP_LINK_URL", url.toString())
//
//        val pendingAcceptIntent = PendingIntent.getBroadcast(
//            this, 1,
//            acceptIntent, 0
//        )
//
//        val pendingDeclineIntent = PendingIntent.getBroadcast(
//            this, 2,
//            rejectIntent, 0
//        )
////
////        notificationView.setOnClickPendingIntent(
////            R.id.btn_accept,
////            pendingAcceptIntent
////        )
////
////        notificationView.setOnClickPendingIntent(
////            R.id.btn_reject,
////            pendingDeclineIntent
////        )
//
//
//// Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }


        notificationManager.notify(AppConstant.NOTIFICATION_ID, builder.build())
    }

    class SwitchButtonListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val action = intent?.action
            //Log.d("Here", action.toString())
            val check = action?.split(":")?.get(0)
            val url = action?.split(":")?.get(1)

            if (check.equals("accept")) {
                //downloadFileNotification(context!!)
                //HomeFragment.downloadFile(url.toString(), context)
                val MainActIntent = Intent(context, MainActivity::class.java)
                MainActIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                MainActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MainActIntent.putExtra("DEEP_LINK_URL", url)
                context?.startActivity(MainActIntent)
                clearNotification(context)

            } else if (check.equals("reject")) {

                clearNotification(context)
            }
        }

        fun clearNotification(context: Context?) {
            val notificationManager =
                context?.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(AppConstant.NOTIFICATION_ID)
        }
    }


}