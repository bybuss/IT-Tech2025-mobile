package bob.colbaskin.it_tech2025.auth.domain.auth

import bob.colbaskin.it_tech2025.common.ApiResult

interface AuthRepository {
    suspend fun login(email: String): ApiResult<Unit>
    suspend fun confirmLogin(code: String, email: String): ApiResult<Unit>
}
