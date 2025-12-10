package bob.colbaskin.it_tech2025.profile.domain

import bob.colbaskin.it_tech2025.profile.data.models.UserDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileService {

    @GET("/api/v1/user/me")
    suspend fun getUser(): UserDTO

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<Unit>
}