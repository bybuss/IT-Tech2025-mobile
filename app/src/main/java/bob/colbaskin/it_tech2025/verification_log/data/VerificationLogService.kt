package bob.colbaskin.it_tech2025.verification_log.data

import bob.colbaskin.it_tech2025.verification_log.data.models.VerificationLogResponse
import retrofit2.http.GET

interface VerificationLogService {

    @GET("/api/v1/document")
    suspend fun getDocuments(): VerificationLogResponse
}
