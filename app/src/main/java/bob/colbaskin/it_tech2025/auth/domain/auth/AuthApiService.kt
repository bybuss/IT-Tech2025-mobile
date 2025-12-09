package bob.colbaskin.it_tech2025.auth.domain.auth

import bob.colbaskin.it_tech2025.auth.data.models.ConfirmLoginBody
import bob.colbaskin.it_tech2025.auth.data.models.LoginBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): Response<Unit>

    @POST("/api/v1/auth/confirm-login")
    suspend fun confirmLogin(
        @Body body: ConfirmLoginBody
    ): Response<Unit>
}
