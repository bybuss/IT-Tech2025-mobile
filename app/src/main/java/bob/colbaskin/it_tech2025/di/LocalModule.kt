package bob.colbaskin.it_tech2025.di

import android.content.Context
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserPreferencesSerializer
import bob.colbaskin.it_tech2025.di.token.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideUserPreferencesSerializer(): UserPreferencesSerializer {
        return UserPreferencesSerializer
    }

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context = context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context = context)
    }
}
