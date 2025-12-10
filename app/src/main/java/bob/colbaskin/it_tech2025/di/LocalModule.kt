package bob.colbaskin.it_tech2025.di

import android.content.Context
import androidx.room.Room
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserPreferencesSerializer
import bob.colbaskin.it_tech2025.di.token.TokenManager
import bob.colbaskin.it_tech2025.scanner.data.local.ScannerDatabase
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

    @Provides
    @Singleton
    fun provideScannerDatabase(@ApplicationContext context: Context): ScannerDatabase {
        return Room.databaseBuilder(
            context,
            ScannerDatabase::class.java,
            ScannerDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideScannerResultDao(database: ScannerDatabase) = database.scannerResultDao()
}
