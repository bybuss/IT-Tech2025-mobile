package bob.colbaskin.it_tech2025.profile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val email: String
)
