package bob.colbaskin.it_tech2025.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmLoginBody(
    val code: String,
    val email: String
)
