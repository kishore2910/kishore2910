package com.powersoaps.distributorsales.ui.main.activity.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.ui.main.activity.introscreens.SplashActivity
import com.powersoaps.distributorsales.ui.utils.Session
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessageReceiver : FirebaseMessagingService() {

    lateinit var intent : Intent
    private val localBroadcastManager: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(
            message.data.getOrElse("title") { null },
            message.data.getOrElse("message") { null },
            message.data.getOrElse("notification_for") { null }
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Session.fireBaseToken = token
    }

    private fun sendNotification(title: String?, message: String?, notificationType: String?) {
        intent = Intent(this, SplashActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(resources.getString(R.string.channel_id), name, importance)
                mChannel.description = descriptionText
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, resources.getString(R.string.channel_id)).apply {
                setSmallIcon(R.mipmap.ic_launcherwhite)
                setContentTitle(title)
                setContentText(message)
                setStyle(NotificationCompat.BigTextStyle().bigText(message))
                setAutoCancel(true)
                setOngoing(false)
                setContentIntent(pendingIntent)
            }
            with(NotificationManagerCompat.from(this)) {
                if (Session.messageScreen) {
                    notify(Random.nextInt(), notificationBuilder.build())
                } else {
                    if (!notificationType.equals("Chat", true)) {
                        notify(Random.nextInt(), notificationBuilder.build())
                    }
                }
            }

            if (Session.isAppInForeground) {
                if (notificationType.equals("NotificationActivity", true)) {
                    localBroadcastManager.sendBroadcast(Intent(Session.NotificationHandle).apply { putExtra("type", "Order") })
                }
//                else  if (notificationType.equals("connection_request", true)) {
////                    localBroadcastManager.sendBroadcast(Intent(Utility.NotificationHandle).apply { putExtra("type", "connection_request").putExtra("value", nonDabbUserId) })
//                }
            }
        }
    }