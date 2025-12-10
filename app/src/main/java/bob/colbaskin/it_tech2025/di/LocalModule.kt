package bob.colbaskin.it_tech2025.di

import android.content.Context
import androidx.room.Room
import bob.colbaskin.it_tech2025.common.biometric.BiometricAuthManager
//import bob.colbaskin.it_tech2025.common.biometric.BiometricAuthManager
import bob.colbaskin.it_tech2025.common.biometric.KeyStoreManager
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.it_tech2025.common.user_prefs.data.datastore.UserPreferencesSerializer
import bob.colbaskin.it_tech2025.di.token.TokenManager
import bob.colbaskin.it_tech2025.scanner.data.local.ScannerDatabase
import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.EncryptedDatabase
import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.EncryptedDatabaseImpl
import bob.colbaskin.it_tech2025.verification_log.data.encrypted_db.VerificationLogDao
import bob.colbaskin.it_tech2025.verification_log.data.notifications.NotificationManager
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
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideScannerResultDao(database: ScannerDatabase) = database.scannerResultDao()

    @Provides
    @Singleton
    fun provideEncryptedDatabase(
        @ApplicationContext context: Context,
        keyStoreManager: KeyStoreManager
    ): EncryptedDatabase {
        return EncryptedDatabaseImpl(context, keyStoreManager)
    }

    @Provides
    @Singleton
    fun provideVerificationLogDao(encryptedDatabase: EncryptedDatabase): VerificationLogDao {
        return encryptedDatabase.verificationLogDao()
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return NotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideBiometricAuthManager(
        @ApplicationContext context: Context
    ): BiometricAuthManager {
        return BiometricAuthManager(context)
    }

    @Provides
    fun provideKeyStoreManager(@ApplicationContext context: Context): KeyStoreManager {
        return KeyStoreManager(context)
    }
}
