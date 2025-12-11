package bob.colbaskin.it_tech2025.verification_log.data

import android.util.Log
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.utils.safeApiCall
import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.VerificationLogDao
import bob.colbaskin.it_tech2025.verification_log.data.models.toDomain
import bob.colbaskin.it_tech2025.verification_log.data.models.toEntity
import bob.colbaskin.it_tech2025.verification_log.domain.VerificationLogRepository
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "VerificationLogRepo"

class VerificationLogRepositoryImpl @Inject constructor(
    private val verificationLogDao: VerificationLogDao,
    private val verificationLogService: VerificationLogService
) : VerificationLogRepository {

    override fun getAll(): Flow<List<VerificationLog>> {
        return verificationLogDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByStatus(status: String): Flow<List<VerificationLog>> {
        return verificationLogDao.getByStatus(status).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncWithServer(): ApiResult<Unit> {
        return safeApiCall(
            apiCall = { verificationLogService.getDocuments() },
            successHandler = { response ->
                val entities = response.items.map { it.toEntity() }

                verificationLogDao.insertAll(entities)

                Log.d(TAG, "Synced ${entities.size} documents from server")
                Unit
            }
        )
    }

    override suspend fun getExpiringSoon(): List<VerificationLog> {
        val threshold = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
        return verificationLogDao.getExpiringSoon(threshold).map { it.toDomain() }
    }

    override suspend fun getCount(): Int {
        return verificationLogDao.getCount()
    }

    override suspend fun forceRefresh(): ApiResult<Unit> {
        return syncWithServer()
    }
}
