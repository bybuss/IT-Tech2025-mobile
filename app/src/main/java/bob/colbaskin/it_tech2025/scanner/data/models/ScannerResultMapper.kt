package bob.colbaskin.it_tech2025.scanner.data.models

import bob.colbaskin.it_tech2025.scanner.data.local.ScannerResultEntity
import bob.colbaskin.it_tech2025.scanner.domain.models.ScannerResult
import java.util.Date

object ScannerResultMapper {

    fun toEntity(dto: DocumentCheckResponse): ScannerResultEntity {
        return ScannerResultEntity(
            documentId = dto.id,
            status = dto.status,
            expirationDate = dto.expirationDate,
            createdAt = dto.createdAt,
            checkedAt = dto.checkedAt,
        )
    }

    fun toDomain(entity: ScannerResultEntity): ScannerResult {
        return ScannerResult(
            documentId = entity.documentId,
            status = entity.status,
            expirationDate = entity.expirationDate,
            createdAt = entity.createdAt,
            checkedAt = entity.checkedAt
        )
    }

    fun toDomain(dto: DocumentCheckResponse): ScannerResult {
        return ScannerResult(
            documentId = dto.id,
            status = dto.status,
            expirationDate = dto.expirationDate,
            createdAt = dto.createdAt,
            checkedAt = dto.checkedAt
        )
    }
}
