package bob.colbaskin.it_tech2025.verification_log.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import bob.colbaskin.it_tech2025.verification_log.domain.VerificationLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "VerificationLogSyncWorker"

class VerificationLogSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: VerificationLogRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting synchronization worker")

        try {
            val result = repository.syncWithServer()

            when (result) {
                is bob.colbaskin.it_tech2025.common.ApiResult.Success -> {
                    Log.d(TAG, "Synchronization completed successfully")
                    Result.success()
                }
                is bob.colbaskin.it_tech2025.common.ApiResult.Error -> {
                    Log.e(TAG, "Synchronization error: ${result.title} - ${result.text}")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Synchronization failed with exception: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "verification_log_sync"
        private const val SYNC_INTERVAL_HOURS = 4L

        fun schedulePeriodicSync(workManager: WorkManager) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()

            val syncWorkRequest: PeriodicWorkRequest =
                PeriodicWorkRequestBuilder<VerificationLogSyncWorker>(
                    SYNC_INTERVAL_HOURS, TimeUnit.HOURS,
                    15, TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )

            Log.d(TAG, "Scheduled periodic sync every $SYNC_INTERVAL_HOURS hours")
        }

        fun enqueueOneTimeSync(workManager: WorkManager) {
            val syncWorkRequest: OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<VerificationLogSyncWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()

            workManager.enqueueUniqueWork(
                "verification_log_one_time_sync",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )
        }
    }
}
