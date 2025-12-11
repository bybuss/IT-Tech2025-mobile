package bob.colbaskin.it_tech2025.verification_log.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationLogResponse(
    @SerialName("items")
    val items: List<VerificationLogDTO>
)

@Serializable
data class VerificationLogDTO(
    @SerialName("document_id")
    val documentId: Long,

    @SerialName("status")
    val status: String,

    @SerialName("expiration_date")
    val expirationDate: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("checked_at")
    val checkedAt: String
)
