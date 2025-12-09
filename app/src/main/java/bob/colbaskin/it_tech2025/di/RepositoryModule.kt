package bob.colbaskin.it_tech2025.di

import bob.colbaskin.it_tech2025.auth.data.AuthRepositoryImpl
import bob.colbaskin.it_tech2025.auth.data.RefreshTokenRepositoryImpl
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthApiService
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthRepository
import bob.colbaskin.it_tech2025.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.it_tech2025.auth.domain.token.RefreshTokenService
import bob.colbaskin.it_tech2025.common.user_prefs.data.UserPreferencesRepositoryImpl
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.it_tech2025.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.it_tech2025.di.token.TokenManager
import bob.colbaskin.it_tech2025.profile.data.ProfileRepositoryImpl
import bob.colbaskin.it_tech2025.profile.domain.ProfileRepository
import bob.colbaskin.it_tech2025.profile.domain.ProfileService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: UserDataStore): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApiService,
        userPreferences: UserPreferencesRepository,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApi = authApi,
            userPreferences = userPreferences
        )
    }

    @Provides
    @Singleton
    fun provideRefreshTokenService(retrofit: Retrofit): RefreshTokenService {
        return retrofit.create(RefreshTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenRepository(
        tokenApi: RefreshTokenService
    ): RefreshTokenRepository {
        return RefreshTokenRepositoryImpl(
            tokenApi = tokenApi
        )
    }

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository (profileApi: ProfileService): ProfileRepository {
        return ProfileRepositoryImpl(profileApi = profileApi)
    }
}
