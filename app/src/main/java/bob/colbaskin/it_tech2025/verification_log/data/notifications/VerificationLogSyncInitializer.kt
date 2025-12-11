package bob.colbaskin.it_tech2025.verification_log.data.notifications

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import bob.colbaskin.it_tech2025.verification_log.data.sync.ExpirationCheckWorker
import bob.colbaskin.it_tech2025.verification_log.data.sync.VerificationLogSyncWorker

class VerificationLogSyncInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val workManager = WorkManager.getInstance(context)

        VerificationLogSyncWorker.schedulePeriodicSync(workManager)

        val expirationCheckRequest = ExpirationCheckWorker.createDailyCheckRequest()
        workManager.enqueue(expirationCheckRequest)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(WorkManagerInitializer::class.java)
    }
}
