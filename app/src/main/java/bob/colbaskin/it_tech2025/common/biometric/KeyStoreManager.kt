package bob.colbaskin.it_tech2025.common.biometric

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val TAG = "KeyStoreManager"
private const val KEY_ALIAS = "verification_log_encryption_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val TRANSFORMATION = "AES/GCM/NoPadding"

class KeyStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    fun getOrCreateKey(): SecretKey {
        return try {
            (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
                ?: createKey()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting key: ${e.message}")
            createKey()
        }
    }

    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            // УБИРАЕМ требование биометрии для ключа БД
            // .setUserAuthenticationRequired(true)
            // .setUserAuthenticationValidityDurationSeconds(60)
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    fun getCipherForEncryption(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        return cipher
    }

    fun getCipherForDecryption(iv: ByteArray? = null): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = getOrCreateKey()

        if (iv != null) {
            val spec = javax.crypto.spec.GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }

        return cipher
    }

    fun isKeyAvailable(): Boolean {
        return try {
            keyStore.containsAlias(KEY_ALIAS)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteKey() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting key: ${e.message}")
        }
    }
}
