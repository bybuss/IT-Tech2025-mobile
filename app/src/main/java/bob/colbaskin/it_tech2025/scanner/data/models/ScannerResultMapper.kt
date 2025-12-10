package bob.colbaskin.it_tech2025.scanner.data.models

import bob.colbaskin.it_tech2025.scanner.data.local.ScannerResultEntity
import bob.colbaskin.it_tech2025.scanner.domain.models.ScannerResult
import java.util.Date

object ScannerResultMapper {

    fun toEntity(
        dto: DocumentCheckResponse,
        hash: String,
        scannedAt: Date = Date()
    ): ScannerResultEntity {
        return ScannerResultEntity(
            documentId = dto.id,
            hash = hash,
            status = dto.status,
            expirationDate = dto.expirationDate,
            createdAt = dto.createdAt,
            checkedAt = dto.checkedAt,
            scannedAt = scannedAt
        )
    }

    fun toDomain(entity: ScannerResultEntity): ScannerResult {
        return ScannerResult(
            documentId = entity.documentId,
            hash = entity.hash,
            status = entity.status,
            expirationDate = entity.expirationDate,
            createdAt = entity.createdAt,
            checkedAt = entity.checkedAt,
            scannedAt = entity.scannedAt
        )
    }

    fun toDomain(
        dto: DocumentCheckResponse,
        hash: String,
        scannedAt: Date = Date()
    ): ScannerResult {
        return ScannerResult(
            documentId = dto.id,
            hash = hash,
            status = dto.status,
            expirationDate = dto.expirationDate,
            createdAt = dto.createdAt,
            checkedAt = dto.checkedAt,
            scannedAt = scannedAt
        )
    }
}