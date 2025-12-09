package bob.colbaskin.it_tech2025.auth.data

import android.util.Log
import bob.colbaskin.it_tech2025.auth.data.models.LoginBody
import bob.colbaskin.it_tech2025.auth.data.models.LoginDTO
import bob.colbaskin.it_tech2025.auth.data.models.RegisterBody
import bob.colbaskin.it_tech2025.auth.data.models.RegisterDTO
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthApiService
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthRepository
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.it_tech2025.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.it_tech2025.common.utils.safeApiCall
import bob.colbaskin.it_tech2025.di.token.TokenManager
import jakarta.inject.Inject

private const val TAG = "Auth"

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val userPreferences: UserPreferencesRepository,
    private val tokenManager: TokenManager
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting login for user: $email")
        return safeApiCall<LoginDTO, Unit>(
            apiCall = {
                authApi.login(
                    body = LoginBody(
                        email = email,
                        password = password
                    )
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Login successful. Saving Authenticated status")
                tokenManager.saveTokens(response.sessionToken)
                userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
                response
            }
        )
    }

    override suspend fun register(
        email: String,
        password: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting register for user: $email")
        return safeApiCall<RegisterDTO, Unit>(
            apiCall = {
                authApi.register(
                    body = RegisterBody(
                        email = email,
                        password = password
                    )
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Register successful. Saving Authenticated status")
                tokenManager.saveTokens(response.sessionToken)
                userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
                response
            }
        )
    }
}
