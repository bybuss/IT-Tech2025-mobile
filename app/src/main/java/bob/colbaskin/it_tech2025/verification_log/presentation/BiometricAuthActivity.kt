package bob.colbaskin.it_tech2025.verification_log.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import bob.colbaskin.it_tech2025.common.biometric.BiometricAuthManager
import bob.colbaskin.it_tech2025.common.design_system.theme.ITTech205Theme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BiometricAuthActivity : FragmentActivity() {

    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager

    companion object {
        const val RESULT_AUTH_SUCCESS = 1001
        const val RESULT_AUTH_FAILED = 1002
        const val RESULT_AUTH_CANCELLED = 1003
        const val EXTRA_ERROR_MESSAGE = "error_message"
        const val EXTRA_NEEDS_BIO_SETUP = "needs_bio_setup"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            if (!biometricAuthManager.isBiometricKeyAvailable()) {
                biometricAuthManager.createBiometricKey()
            }
        }

        setContent {
            ITTech205Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BiometricAuthScreen(
                        onAuthSuccess = {
                            setResult(RESULT_AUTH_SUCCESS)
                            finish()
                        },
                        onAuthFailed = { errorMessage, needsBioSetup ->
                            val intent = Intent().apply {
                                putExtra(EXTRA_ERROR_MESSAGE, errorMessage)
                                putExtra(EXTRA_NEEDS_BIO_SETUP, needsBioSetup)
                            }
                            setResult(RESULT_AUTH_FAILED, intent)
                            finish()
                        },
                        onAuthCancelled = {
                            setResult(RESULT_AUTH_CANCELLED)
                            finish()
                        }
                    )
                }
            }
        }

        lifecycleScope.launch {
            startBiometricAuth()
        }
    }

    private fun startBiometricAuth() {
        val biometricManager = BiometricManager.from(this)
        val authResult = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )

        when (authResult) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (!biometricAuthManager.isBiometricKeyAvailable()) {
                    biometricAuthManager.createBiometricKey()
                }
                showBiometricPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val intent = Intent().apply {
                    putExtra(EXTRA_ERROR_MESSAGE, "Настройте биометрическую аутентификацию в настройках устройства")
                    putExtra(EXTRA_NEEDS_BIO_SETUP, true)
                }
                setResult(RESULT_AUTH_FAILED, intent)
                finish()
            }
            else -> {
                val intent = Intent().apply {
                    putExtra(EXTRA_ERROR_MESSAGE, "Биометрическая аутентификация недоступна")
                    putExtra(EXTRA_NEEDS_BIO_SETUP, false)
                }
                setResult(RESULT_AUTH_FAILED, intent)
                finish()
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricAuthManager.saveAuthenticationSuccess()
                    setResult(RESULT_AUTH_SUCCESS)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val intent = Intent().apply {
                        putExtra(EXTRA_ERROR_MESSAGE, "Аутентификация не удалась. Попробуйте снова.")
                        putExtra(EXTRA_NEEDS_BIO_SETUP, false)
                    }
                    setResult(RESULT_AUTH_FAILED, intent)
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            setResult(RESULT_AUTH_CANCELLED)
                        }
                        else -> {
                            val intent = Intent().apply {
                                putExtra(EXTRA_ERROR_MESSAGE, "Ошибка: $errString")
                                putExtra(EXTRA_NEEDS_BIO_SETUP, false)
                            }
                            setResult(RESULT_AUTH_FAILED, intent)
                        }
                    }
                    finish()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Доступ к журналу верификаций")
            .setSubtitle("Подтвердите свою личность")
            .setDescription("Используйте отпечаток пальца или Face ID для доступа")
            .setNegativeButtonText("Отмена")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
fun BiometricAuthScreen(
    onAuthSuccess: () -> Unit,
    onAuthFailed: (String, Boolean) -> Unit,
    onAuthCancelled: () -> Unit
) {
    val context = LocalContext.current
    var authState by remember { mutableStateOf(AuthState.IDLE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = when (authState) {
                AuthState.SUCCESS -> MaterialTheme.colorScheme.primary
                AuthState.ERROR -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Биометрическая аутентификация",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            modifier = Modifier.padding(24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ожидание подтверждения...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

enum class AuthState {
    IDLE,
    SUCCESS,
    ERROR
}
