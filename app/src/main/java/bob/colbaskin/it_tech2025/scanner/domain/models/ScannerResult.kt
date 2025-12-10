package bob.colbaskin.it_tech2025.scanner.domain.models

import java.util.Date

data class ScannerResult(
    val documentId: Long,
    val hash: String,
    val status: String,
    val expirationDate: String,
    val createdAt: String,
    val checkedAt: String,
    val scannedAt: Date
) {
    val statusColor: StatusColor
        get() = when (status.lowercase()) {
            "green" -> StatusColor.GREEN
            "yellow" -> StatusColor.YELLOW
            "red" -> StatusColor.RED
            else -> StatusColor.GRAY
        }

    val formattedExpirationDate: String
        get() = try {
            val isoDate = java.time.Instant.parse(expirationDate)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
            isoDate.atZone(java.time.ZoneId.systemDefault()).format(formatter)
        } catch (e: Exception) {
            expirationDate
        }
}

