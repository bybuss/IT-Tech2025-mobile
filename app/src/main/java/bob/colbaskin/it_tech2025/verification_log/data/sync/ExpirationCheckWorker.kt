package bob.colbaskin.it_tech2025.verification_log.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import bob.colbaskin.it_tech2025.verification_log.data.notifications.NotificationManager
import bob.colbaskin.it_tech2025.verification_log.domain.VerificationLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val TAG = "ExpirationCheckWorker"

class ExpirationCheckWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: VerificationLogRepository,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting expiration check worker")

        try {
            val expiringSoonLogs = repository.getExpiringSoon()

            if (expiringSoonLogs.isNotEmpty()) {
                notificationManager.showExpiringDocumentsNotification(expiringSoonLogs)
                Log.d(TAG, "Found ${expiringSoonLogs.size} expiring documents")
            } else {
                Log.d(TAG, "No expiring documents found")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Expiration check failed: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        fun createDailyCheckRequest(): androidx.work.OneTimeWorkRequest {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 42)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

            return OneTimeWorkRequestBuilder<ExpirationCheckWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("expiration_check")
                .build()
        }
    }
}
