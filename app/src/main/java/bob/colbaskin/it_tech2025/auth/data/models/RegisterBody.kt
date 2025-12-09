package bob.colbaskin.it_tech2025.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val email: String,
    val password: String
)
