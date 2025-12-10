package bob.colbaskin.it_tech2025.scanner.domain.models


data class ScannerResult(
    val documentId: Long,
    val status: String,
    val expirationDate: String,
    val createdAt: String,
    val checkedAt: String,
)
