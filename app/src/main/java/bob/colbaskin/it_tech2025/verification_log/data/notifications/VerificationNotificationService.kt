package bob.colbaskin.it_tech2025.verification_log.data.notifications

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bob.colbaskin.it_tech2025.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NOTIFICATION_ID = 2001
private const val CHANNEL_ID = "verification_log_foreground"

@AndroidEntryPoint
class VerificationNotificationService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SYNC -> startSync()
            ACTION_STOP_SERVICE -> stopSelf()
        }

        createForegroundNotificationChannel()

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startSync() {
        serviceScope.launch {
            stopSelf()
        }
    }

    private fun createForegroundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Синхронизация журнала",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Сервис синхронизации данных"
            }

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Синхронизация журнала")
            .setContentText("Идет синхронизация данных")
            .setSmallIcon(R.drawable.refresh_ccw)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START_SYNC = "ACTION_START_SYNC"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    }
}
