package bob.colbaskin.it_tech2025.profile.domain

import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.profile.domain.models.User

interface ProfileRepository {

    suspend fun getUser(): ApiResult<User>
}