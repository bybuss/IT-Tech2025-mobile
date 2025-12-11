package bob.colbaskin.it_tech2025.verification_log.domain.models

import java.util.Date

data class VerificationLog(
    val documentId: Long,
    val status: DocumentStatus,
    val expirationDate: Date,
    val createdAt: Date,
    val checkedAt: Date
) {
    fun isExpiringSoon(): Boolean {
        val now = Date()
        val created = createdAt
        val expires = expirationDate

        val totalDuration = expires.time - created.time
        val warningThreshold = created.time + (totalDuration * 0.9)

        return now.time >= warningThreshold && now.time < expires.time
    }
}

enum class DocumentStatus(val color: String) {
    GREEN("green"),
    YELLOW("yellow"),
    RED("red");

    companion object {
        fun fromString(value: String): DocumentStatus {
            return when (value.lowercase()) {
                "green" -> GREEN
                "yellow" -> YELLOW
                "red" -> RED
                else -> RED
            }
        }
    }
}
