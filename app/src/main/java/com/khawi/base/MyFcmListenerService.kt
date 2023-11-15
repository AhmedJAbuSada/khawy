package com.khawi.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.khawi.R
import com.khawi.SplashActivity
import java.util.Random


class MyFcmListenerService : FirebaseMessagingService() {
    companion object {
        val trackMessagingLiveData = MutableLiveData<String>()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title: String?
        val data = message.data
        try {
            title = message.notification?.title ?: (data["title"] ?: "")

            Log.e("FCM", "onMessageReceived: ${message.notification.toString()}")
            val intent = Intent(applicationContext, SplashActivity::class.java)
            intent.putExtra("notification", "notification")
            showNotification(
                if (!TextUtils.isEmpty(title))
                    title
                else getString(R.string.app_name),
                message.notification?.body ?: "",
                intent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showNotification(title: String, body: String, contentIntent: Intent) {
        val remotePicture: Bitmap? = null
        var notifyStyle: NotificationCompat.BigPictureStyle? = null
        try {
            if (remotePicture != null) {
                notifyStyle = NotificationCompat.BigPictureStyle()
                notifyStyle.setSummaryText(body)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val n = getNotification(title, body, contentIntent, notifyStyle)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.app_name),
                getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }


        triggerNotification(this@MyFcmListenerService, n)
    }

    private fun getNotification(
        title: String,
        body: String,
        contentIntent: Intent,
        notifyStyle: NotificationCompat.BigPictureStyle?
    ): Notification {

        val parseBuilder = NotificationCompat.Builder(this, getString(R.string.app_name))
        parseBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(body))
        val random = Random()
        val pContentIntent = PendingIntent.getActivity(
            this, random.nextInt(), contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        parseBuilder.setContentTitle(title).setContentText(body).setTicker(title)
            .setColor(Color.parseColor("#FF0000"))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pContentIntent).setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)


        if (notifyStyle != null) {
            parseBuilder.setStyle(notifyStyle)
        }

        return parseBuilder.build()

    }

    private fun triggerNotification(context: Context?, notification: Notification?) {
        try {
            if (context != null && notification != null) {
                val nm =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = System.currentTimeMillis().toInt()
                try {
                    notification.priority = Notification.PRIORITY_MAX
                    notification.defaults =
                        Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldError) {
                    e.printStackTrace()
                }

                nm.notify(notificationId, notification)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: NoSuchFieldError) {
            e.printStackTrace()
        }

    }
}
