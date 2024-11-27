package com.ibs.spato.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ibs.spato.R
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale




class FirebaseMessaging : FirebaseMessagingService() {
    lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private lateinit var pendingIntent: PendingIntent
    private lateinit var session: Session

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @SuppressLint("NewApi")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.notification != null) {
            pushNotification(message.notification!!)
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pushNotification(notification: RemoteMessage.Notification) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent()
        pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_IMMUTABLE)

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Custom Channel"
            notificationChannel = NotificationChannel(Constants.CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }

        session = Session(this)
        if (session.getString(Constants.NOTIFICATION) != Constants.OFF) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            notificationManagerCompat.notify(1234, builder.build())
        }
    }
}