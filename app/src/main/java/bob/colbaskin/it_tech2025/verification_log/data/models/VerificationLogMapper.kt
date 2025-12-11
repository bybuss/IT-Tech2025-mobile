package bob.colbaskin.it_tech2025.verification_log.data.models

import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.VerificationLogEntity
import bob.colbaskin.it_tech2025.verification_log.domain.models.DocumentStatus
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun VerificationLogDTO.toDomain(): VerificationLog {
    return VerificationLog(
        documentId = documentId,
        status = DocumentStatus.fromString(status),
        expirationDate = dateFormat.parse(expirationDate) ?: Date(),
        createdAt = dateFormat.parse(createdAt) ?: Date(),
        checkedAt = dateFormat.parse(checkedAt) ?: Date()
    )
}

fun VerificationLogDTO.toEntity(): VerificationLogEntity {
    return VerificationLogEntity(
        documentId = documentId,
        status = status,
        expirationDate = dateFormat.parse(expirationDate) ?: Date(),
        createdAt = dateFormat.parse(createdAt) ?: Date(),
        checkedAt = dateFormat.parse(checkedAt) ?: Date()
    )
}

fun VerificationLogEntity.toDomain(): VerificationLog {
    return VerificationLog(
        documentId = documentId,
        status = DocumentStatus.fromString(status),
        expirationDate = expirationDate,
        createdAt = createdAt,
        checkedAt = checkedAt
    )
}

fun VerificationLog.toEntity(): VerificationLogEntity {
    return VerificationLogEntity(
        documentId = documentId,
        status = status.color,
        expirationDate = expirationDate,
        createdAt = createdAt,
        checkedAt = checkedAt
    )
}
