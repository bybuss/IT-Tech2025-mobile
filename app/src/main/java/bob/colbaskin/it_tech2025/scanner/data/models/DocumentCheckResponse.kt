package bob.colbaskin.it_tech2025.scanner.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentCheckResponse(
    val id: Long,
    val status: String,
    @SerialName("expiration_date") val expirationDate: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("checked_at") val checkedAt: String
)
