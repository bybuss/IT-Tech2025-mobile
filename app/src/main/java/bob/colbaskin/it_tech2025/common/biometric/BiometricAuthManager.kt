package bob.colbaskin.it_tech2025.common.biometric

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BiometricAuthManager"
private const val AUTH_KEY_ALIAS = "biometric_auth_key"
private const val AUTH_KEY_VALIDITY_DURATION = 30 // секунды - время валидности аутентификации
private const val PREFS_NAME = "auth_prefs"
private const val PREFS_KEY_AUTH_TIMESTAMP = "last_auth_timestamp"
private const val PREFS_KEY_AUTH_USER_ID = "auth_user_id"

@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Создает ключ для биометрической аутентификации
     */
    fun createBiometricKey() {
        try {
            // Удаляем старый ключ, если есть
            if (keyStore.containsAlias(AUTH_KEY_ALIAS)) {
                keyStore.deleteEntry(AUTH_KEY_ALIAS)
            }

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )

            val keySpec = KeyGenParameterSpec.Builder(
                AUTH_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeySize(256)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(30) // 30 секунд для демонстрации
                .setInvalidatedByBiometricEnrollment(true)
                .build()

            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
            Log.d(TAG, "Biometric key created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create biometric key: ${e.message}", e)
        }
    }

    /**
     * Проверяет, настроена ли биометрическая аутентификация
     */
    fun isBiometricKeyAvailable(): Boolean {
        return try {
            keyStore.containsAlias(AUTH_KEY_ALIAS)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Проверяет, действительна ли текущая аутентификация для указанного пользователя
     */
    fun isAuthenticated(userId: String? = null): Boolean {
        val lastAuthTime = sharedPrefs.getLong(PREFS_KEY_AUTH_TIMESTAMP, 0)
        val savedUserId = sharedPrefs.getString(PREFS_KEY_AUTH_USER_ID, null)
        val currentTime = System.currentTimeMillis()

        // Проверяем:
        // 1. Время аутентификации (действительна 30 минут)
        // 2. ID пользователя (если указан)
        val isTimeValid = (currentTime - lastAuthTime) < 30 * 60 * 1000
        val isUserValid = userId == null || userId == savedUserId

        return isTimeValid && isUserValid
    }

    /**
     * Сохраняет время успешной аутентификации для пользователя
     */
    fun saveAuthenticationSuccess(userId: String? = null) {
        sharedPrefs.edit()
            .putLong(PREFS_KEY_AUTH_TIMESTAMP, System.currentTimeMillis())
            .apply()

        if (userId != null) {
            sharedPrefs.edit()
                .putString(PREFS_KEY_AUTH_USER_ID, userId)
                .apply()
        }

        Log.d(TAG, "Authentication saved for user: $userId")
    }

    /**
     * Сбрасывает аутентификацию
     */
    fun clearAuthentication() {
        sharedPrefs.edit()
            .remove(PREFS_KEY_AUTH_TIMESTAMP)
            .remove(PREFS_KEY_AUTH_USER_ID)
            .apply()
        Log.d(TAG, "Authentication cleared")
    }

    /**
     * Получает ID текущего аутентифицированного пользователя
     */
    fun getAuthenticatedUserId(): String? {
        return sharedPrefs.getString(PREFS_KEY_AUTH_USER_ID, null)
    }

    /**
     * Удаляет биометрический ключ
     */
    fun deleteBiometricKey() {
        try {
            if (keyStore.containsAlias(AUTH_KEY_ALIAS)) {
                keyStore.deleteEntry(AUTH_KEY_ALIAS)
                Log.d(TAG, "Biometric key deleted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting biometric key: ${e.message}")
        }
    }
}
