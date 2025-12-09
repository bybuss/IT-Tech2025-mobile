package bob.colbaskin.it_tech2025.auth.domain.token

import bob.colbaskin.it_tech2025.common.ApiResult

interface RefreshTokenRepository {

    suspend fun refresh(): ApiResult<Unit>
}
