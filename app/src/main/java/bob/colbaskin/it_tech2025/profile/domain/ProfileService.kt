package bob.colbaskin.it_tech2025.profile.domain

import bob.colbaskin.it_tech2025.profile.data.models.UserDTO
import retrofit2.http.GET

interface ProfileService {

    @GET("/api/v1/user/me")
    suspend fun getUser(): UserDTO
}