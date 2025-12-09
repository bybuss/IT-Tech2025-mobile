package bob.colbaskin.it_tech2025.profile.data.models

import bob.colbaskin.it_tech2025.profile.domain.models.User

fun UserDTO.toDomain(): User {
    return User(
        userId = this.id,
        email = this.email
    )
}
