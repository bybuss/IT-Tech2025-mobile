package bob.colbaskin.it_tech2025.scanner.data.remote

import android.util.Log
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.utils.safeApiCall
import bob.colbaskin.it_tech2025.scanner.data.local.ScannerResultDao
import bob.colbaskin.it_tech2025.scanner.data.models.DocumentCheckResponse
import bob.colbaskin.it_tech2025.scanner.data.models.ScannerResultMapper
import bob.colbaskin.it_tech2025.scanner.domain.ScannerRepository
import bob.colbaskin.it_tech2025.scanner.domain.models.ScannerResult
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ScannerRepositoryImpl"

@Singleton
class ScannerRepositoryImpl @Inject constructor(
    private val documentApi: DocumentApi,
    private val scannerResultDao: ScannerResultDao
) : ScannerRepository {

    override suspend fun checkDocument(hash: String): ApiResult<ScannerResult> {
        Log.d(TAG, "Checking document for hash: ${hash.take(20)}...")

        return safeApiCall<Response<DocumentCheckResponse>, ScannerResult>(
            apiCall = {
                documentApi.checkDocument(hash)
            },
            successHandler = { response ->
                val dto = response.body()!!
                val domainResult = ScannerResultMapper.toDomain(dto)
                saveScanResult(domainResult)
                domainResult
            }
        )
    }

    override suspend fun saveScanResult(result: ScannerResult) {
        try {
            val entity = ScannerResultMapper.toEntity(
                DocumentCheckResponse(
                    id = result.documentId,
                    status = result.status,
                    expirationDate = result.expirationDate,
                    createdAt = result.createdAt,
                    checkedAt = result.checkedAt
                )
            )

            scannerResultDao.insertResult(entity)
            Log.d(TAG, "Scan result saved to DB: ${result.documentId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving scan result to DB", e)
        }
    }

    override suspend fun getAllScanResults(): List<ScannerResult> {
        return try {
            scannerResultDao.getAllResults().map { ScannerResultMapper.toDomain(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all scan results", e)
            emptyList()
        }
    }
}
