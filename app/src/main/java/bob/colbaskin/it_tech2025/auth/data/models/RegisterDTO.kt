package bob.colbaskin.it_tech2025.auth.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDTO(
    @SerialName("id") val userId: Int,
    @SerialName("session_token") val sessionToken: String,
)
