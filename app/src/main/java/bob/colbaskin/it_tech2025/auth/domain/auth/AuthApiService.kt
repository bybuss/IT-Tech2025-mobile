package bob.colbaskin.it_tech2025.auth.domain.auth

import bob.colbaskin.it_tech2025.auth.data.models.LoginBody
import bob.colbaskin.it_tech2025.auth.data.models.LoginDTO
import bob.colbaskin.it_tech2025.auth.data.models.RegisterBody
import bob.colbaskin.it_tech2025.auth.data.models.RegisterDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): LoginDTO

    @POST("/api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterBody
    ): RegisterDTO
}
