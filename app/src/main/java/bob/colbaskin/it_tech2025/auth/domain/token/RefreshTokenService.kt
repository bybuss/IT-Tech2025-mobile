package bob.colbaskin.it_tech2025.auth.domain.token

import retrofit2.Response
import retrofit2.http.GET

interface RefreshTokenService {

    @GET("/api/refresh")
    suspend fun refresh(): Response<Unit>
}
