package bob.colbaskin.it_tech2025.auth.data

import android.util.Log
import bob.colbaskin.it_tech2025.auth.data.models.ConfirmLoginBody
import bob.colbaskin.it_tech2025.auth.data.models.LoginBody
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthApiService
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthRepository
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.it_tech2025.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.it_tech2025.common.utils.safeApiCall
import jakarta.inject.Inject
import retrofit2.Response

private const val TAG = "Auth"

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val userPreferences: UserPreferencesRepository
): AuthRepository {
    override suspend fun login(email: String): ApiResult<Unit> {
        Log.d(TAG, "Attempting login for user: $email")
        return safeApiCall<Response<Unit>, Unit>(
            apiCall = {
                authApi.login(
                    body = LoginBody(email = email)
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Login successful. Saving Authenticated status")
                response
            }
        )
    }

    override suspend fun confirmLogin(code: String, email: String): ApiResult<Unit> {
        Log.d(TAG, "Attempting confirm login for user: $email & code: $code")
        return safeApiCall<Response<Unit>, Unit>(
            apiCall = {
                authApi.confirmLogin(
                    body = ConfirmLoginBody(code = code, email = email)
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Login confirm successful. Saving Authenticated status")
                userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
                response
            }
        )
    }
}
