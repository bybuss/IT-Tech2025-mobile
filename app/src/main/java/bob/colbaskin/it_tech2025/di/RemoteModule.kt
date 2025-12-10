package bob.colbaskin.it_tech2025.di

import android.content.Context
import android.util.Log
import bob.colbaskin.it_tech2025.BuildConfig
import bob.colbaskin.it_tech2025.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.it_tech2025.di.token.RetryInterceptor
import bob.colbaskin.it_tech2025.di.token.TokenAuthenticator
import bob.colbaskin.it_tech2025.di.token.TokenInterceptor
import bob.colbaskin.it_tech2025.di.token.TokenManager
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    @Named("apiUrl")
    fun provideApiUrl(): String {
        return BuildConfig.BASE_API_URL
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        refreshTokenRepository: Provider<RefreshTokenRepository>
    ): TokenAuthenticator {
        return TokenAuthenticator(
            refreshTokenRepository = refreshTokenRepository
        )
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenManager: TokenManager): TokenInterceptor {
        return TokenInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(
            maxRetries = 3,
            baseDelayMillis = 1000L,
            maxDelayMillis = 10000L
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: Provider<TokenAuthenticator>,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        val cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(retryInterceptor)
            .authenticator { route, response ->
                tokenAuthenticator.get().authenticate(route, response)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d("Cookies", "Sending cookies: ${request.headers["Cookie"]}")
                val response = chain.proceed(request)
                Log.d("Cookies", "Received cookies: ${response.headers["Set-Cookie"]}")
                response
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("apiUrl") apiUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        val jsonConfig = Json {
            ignoreUnknownKeys = true
        }

        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(okHttpClient)
            .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
