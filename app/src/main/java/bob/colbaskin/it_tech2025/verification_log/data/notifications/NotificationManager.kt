package bob.colbaskin.it_tech2025.verification_log.data.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import bob.colbaskin.it_tech2025.R
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val CHANNEL_ID_SYNC = "verification_log_sync"
        private const val CHANNEL_ID_EXPIRATION = "verification_log_expiration"
        private const val NOTIFICATION_ID_SYNC_START = 1001
        private const val NOTIFICATION_ID_SYNC_SUCCESS = 1002
        private const val NOTIFICATION_ID_SYNC_ERROR = 1003
        private const val NOTIFICATION_ID_EXPIRING = 1004

        private const val POST_NOTIFICATIONS_PERMISSION = "android.permission.POST_NOTIFICATIONS"

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val syncChannel = NotificationChannel(
                CHANNEL_ID_SYNC,
                "Синхронизация",
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о процессе синхронизации"
            }

            val expirationChannel = NotificationChannel(
                CHANNEL_ID_EXPIRATION,
                "Истечение документов",
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления об истекающих документах"
            }

            notificationManager.createNotificationChannel(syncChannel)
            notificationManager.createNotificationChannel(expirationChannel)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun safeNotify(notificationId: Int, notification: Notification) {
        try {
            if (hasNotificationPermission()) {
                notificationManager.notify(notificationId, notification)
            } else {
                android.util.Log.d("NotificationManager", "Нет разрешения на отправку уведомлений")
            }
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationManager", "SecurityException при отправке уведомления: ${e.message}")
        }
    }

    fun showSyncStartedNotification() {
        val notification = buildNotification(
            channelId = CHANNEL_ID_SYNC,
            title = "Синхронизация начата",
            text = "Идет синхронизация журнала верификаций",
            notificationId = NOTIFICATION_ID_SYNC_START
        )
        safeNotify(NOTIFICATION_ID_SYNC_START, notification)
    }

    fun showSyncSuccessNotification() {
        val notification = buildNotification(
            channelId = CHANNEL_ID_SYNC,
            title = "Синхронизация завершена",
            text = "Данные успешно обновлены",
            notificationId = NOTIFICATION_ID_SYNC_SUCCESS
        )
        safeNotify(NOTIFICATION_ID_SYNC_SUCCESS, notification)
    }

    fun showSyncErrorNotification(errorMessage: String) {
        val notification = buildNotification(
            channelId = CHANNEL_ID_SYNC,
            title = "Ошибка синхронизации",
            text = "Не удалось синхронизировать данные: $errorMessage",
            notificationId = NOTIFICATION_ID_SYNC_ERROR
        )
        safeNotify(NOTIFICATION_ID_SYNC_ERROR, notification)
    }

    fun showExpiringDocumentsNotification(documents: List<VerificationLog>) {
        if (documents.isEmpty()) return

        val documentsText = if (documents.size <= 3) {
            documents.joinToString("\n") { doc ->
                "• Документ ${doc.documentId} истекает ${dateFormat.format(doc.expirationDate)}"
            }
        } else {
            "• Документ ${documents[0].documentId} истекает ${dateFormat.format(documents[0].expirationDate)}\n" +
                    "• Документ ${documents[1].documentId} истекает ${dateFormat.format(documents[1].expirationDate)}\n" +
                    "• ... и ещё ${documents.size - 2} документов"
        }

        val notification = buildNotification(
            channelId = CHANNEL_ID_EXPIRATION,
            title = "Истекает ${documents.size} документов",
            text = "Следующие документы скоро истекут:\n$documentsText",
            notificationId = NOTIFICATION_ID_EXPIRING
        )
        safeNotify(NOTIFICATION_ID_EXPIRING, notification)
    }

    private fun buildNotification(
        channelId: String,
        title: String,
        text: String,
        notificationId: Int
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }
}
