package com.shreyas.fcmtesting.ui.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shreyas.fcmtesting.R
import com.shreyas.fcmtesting.ui.Reciver.TestReciver
import com.shreyas.fcmtesting.utiels.setCommand
import com.shreyas.fcmtesting.ui.activity.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("test_123", "dummy:  ")

        remoteMessage.data.isNotEmpty().let { it->
            Log.d("firebase_1234", "eret: ${remoteMessage.data.get("Command")}")
            setCommand(
                this,
                remoteMessage.data.get("Command")!!
            )
            val intent = Intent("com.shreyas.fcmtesting_FCM")
            intent.putExtra("fromIntent", remoteMessage.data.get("Command"))
            val lb = LocalBroadcastManager.getInstance(this)
            lb.sendBroadcast(intent)
        }
        remoteMessage.notification.let {

            val filter  = IntentFilter("com.google.firebase.MESSAGING_EVENT")
            registerReceiver(TestReciver(), filter)
            showNotification(it?.body!!, remoteMessage)
        }

    }

    private fun sheduleJob() {
        val work = OneTimeWorkRequest.Builder(Woker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()

    }

    override fun onNewToken(token: String) {
        Log.d("firebase_123", "token: ${token}")

    }

    private fun showNotification(
        messageBody: String,
        remoteMessage: RemoteMessage
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icom_123)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}