package bob.colbaskin.it_tech2025.profile.data

import android.util.Log
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.utils.safeApiCall
import bob.colbaskin.it_tech2025.profile.data.models.UserDTO
import bob.colbaskin.it_tech2025.profile.data.models.toDomain
import bob.colbaskin.it_tech2025.profile.domain.models.User
import bob.colbaskin.it_tech2025.profile.domain.ProfileRepository
import bob.colbaskin.it_tech2025.profile.domain.ProfileService
import retrofit2.Response
import javax.inject.Inject

private const val  TAG = "Profile"

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileService
): ProfileRepository {
    override suspend fun getUser(): ApiResult<User> {
        Log.d(TAG, "Attempting getting User")
        return safeApiCall<UserDTO, User>(
            apiCall = { profileApi.getUser() },
            successHandler = { response ->
                val user = response.toDomain()
                Log.d(TAG, "User got successful: $user")
                user
            }
        )
    }

    override suspend fun logout(): ApiResult<Unit> {
        Log.d(TAG, "Attempting getting User")
        return safeApiCall<Response<Unit>, Unit>(
            apiCall = { profileApi.logout() },
            successHandler = { response ->
                Log.d(TAG, "User got logout")
                response
            }
        )
    }
}
