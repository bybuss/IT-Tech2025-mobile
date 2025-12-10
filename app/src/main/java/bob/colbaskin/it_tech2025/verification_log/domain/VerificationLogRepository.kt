package bob.colbaskin.it_tech2025.verification_log.domain

import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
import kotlinx.coroutines.flow.Flow

interface VerificationLogRepository {

    fun getAll(): Flow<List<VerificationLog>>

    fun getByStatus(status: String): Flow<List<VerificationLog>>

    suspend fun syncWithServer(): ApiResult<Unit>

    suspend fun getExpiringSoon(): List<VerificationLog>

    suspend fun getCount(): Int

    suspend fun forceRefresh(): ApiResult<Unit>
}
