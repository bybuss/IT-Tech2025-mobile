package bob.colbaskin.it_tech2025.di.token

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import kotlin.math.pow

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMillis: Long = 1000L,
    private val maxDelayMillis: Long = 30000L,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var retryCount = 0

        while (retryCount <= maxRetries) {
            try {
                response = chain.proceed(request)

                if (shouldRetry(response, retryCount)) {
                    response.close()
                    retryCount++

                    if (retryCount <= maxRetries) {
                        val delay = calculateDelay(retryCount)
                        logRetry(request, retryCount, delay, response.code)
                        Thread.sleep(delay)
                        continue
                    }
                }

                return response

            } catch (exception: Exception) {
                if (shouldRetry(exception, retryCount) && retryCount < maxRetries) {
                    retryCount++
                    val delay = calculateDelay(retryCount)
                    logRetry(request, retryCount, delay, exception)
                    Thread.sleep(delay)
                } else {
                    throw exception
                }
            }
        }

        return response ?: throw IOException("Max retries ($maxRetries) exceeded")
    }

    private fun shouldRetry(response: Response, retryCount: Int): Boolean {
        return when {
            retryCount >= maxRetries -> false
            response.code in 500..599 -> true
            response.code == 429 -> true
            response.code == 408 -> true
            else -> false
        }
    }

    private fun shouldRetry(exception: Exception, retryCount: Int): Boolean {
        return when (exception) {
            is SocketTimeoutException -> true
            is UnknownHostException -> true
            is SSLHandshakeException -> false
            is IOException -> true
            else -> false
        }
    }

    private fun calculateDelay(retryCount: Int): Long {
        val exponentialDelay = (baseDelayMillis * 2.0.pow(retryCount - 1)).toLong()
        return minOf(exponentialDelay, maxDelayMillis)
    }

    private fun logRetry(request: okhttp3.Request, retryCount: Int, delay: Long, code: Int) {
        Log.i("LOG","Retry #$retryCount for ${request.url} after ${delay}ms (Status: $code)")
    }

    private fun logRetry(request: okhttp3.Request, retryCount: Int, delay: Long, exception: Exception) {
        Log.i("LOG","Retry #$retryCount for ${request.url} after ${delay}ms (Exception: ${exception.javaClass.simpleName})")
    }
}
