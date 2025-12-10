package bob.colbaskin.it_tech2025.di

import android.content.Context
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
import bob.colbaskin.it_tech2025.scanner.data.local.ScannerResultDao
import bob.colbaskin.it_tech2025.scanner.data.remote.DocumentApi
import bob.colbaskin.it_tech2025.scanner.data.remote.ScannerRepositoryImpl
import bob.colbaskin.it_tech2025.scanner.domain.ScannerRepository
import bob.colbaskin.it_tech2025.verification_log.data.VerificationLogRepositoryImpl
import bob.colbaskin.it_tech2025.verification_log.data.VerificationLogService
import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.VerificationLogDao
import bob.colbaskin.it_tech2025.verification_log.data.notifications.NotificationManager
import bob.colbaskin.it_tech2025.verification_log.domain.VerificationLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideDocumentApi(retrofit: Retrofit): DocumentApi {
        return retrofit.create(DocumentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideScannerRepository (
        documentApi: DocumentApi,
        scannerResultDao: ScannerResultDao
    ): ScannerRepository {
        return ScannerRepositoryImpl(
            documentApi = documentApi,
            scannerResultDao = scannerResultDao
        )
    }

    @Provides
    @Singleton
    fun provideVerificationLogService(retrofit: Retrofit): VerificationLogService {
        return retrofit.create(VerificationLogService::class.java)
    }

    @Provides
    @Singleton
    fun provideVerificationLogRepository (
        verificationLogDao: VerificationLogDao,
        verificationLogService: VerificationLogService
    ): VerificationLogRepository {
        return VerificationLogRepositoryImpl(
            verificationLogDao = verificationLogDao,
            verificationLogService = verificationLogService
        )
    }
}
