package bob.colbaskin.it_tech2025.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val email: String,
    val password: String
)
