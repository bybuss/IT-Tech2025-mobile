package bob.colbaskin.it_tech2025.di.token

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "Auth"

class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val sessionToken = tokenManager.getSessionToken()

        Log.d(TAG, "Request to: ${request.url}")
        Log.d(TAG, "Headers: ${request.headers}")

        return if (!sessionToken.isNullOrEmpty()) {
            Log.d(TAG, "Adding session token to headers")
            val authRequest = request.newBuilder()
                .header("Authorization", sessionToken)
                .build()
            chain.proceed(authRequest)
        } else {
            Log.d(TAG, "No session token available")
            chain.proceed(request)
        }
    }
}
