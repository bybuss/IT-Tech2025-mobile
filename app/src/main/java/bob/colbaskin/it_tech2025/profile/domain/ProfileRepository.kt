package bob.colbaskin.it_tech2025.profile.domain

import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.profile.domain.models.User
import retrofit2.Response

interface ProfileRepository {
    suspend fun getUser(): ApiResult<User>
    suspend fun logout(): ApiResult<Unit>
}