package bob.colbaskin.it_tech2025.scanner.domain

import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.scanner.domain.models.ScannerResult

interface ScannerRepository {
    suspend fun checkDocument(hash: String): ApiResult<ScannerResult>
    suspend fun saveScanResult(result: ScannerResult)
    suspend fun getAllScanResults(): List<ScannerResult>
}
