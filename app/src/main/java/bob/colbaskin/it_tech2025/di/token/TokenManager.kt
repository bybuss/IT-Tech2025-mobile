package bob.colbaskin.it_tech2025.di.token

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private const val TOKEN_DAA_STORE_NAME = "token_preferences"
private val Context.tokenDatastore: DataStore<Preferences> by preferencesDataStore(
    name = TOKEN_DAA_STORE_NAME
)
private const val TAG = "Auth"

@Singleton
class TokenManager @Inject constructor (context: Context) {

    companion object {
        private val SESSION_TOKEN = stringPreferencesKey("session_token")
    }

    private val dataStore: DataStore<Preferences> = context.tokenDatastore

    suspend fun saveTokens(sessionToken: String) {
        Log.d(TAG, "Saving tokens. \nSession: $sessionToken,")
        dataStore.edit { prefs ->
            prefs[SESSION_TOKEN] = sessionToken
        }
    }

    private val sessionToken: Flow<String?> = dataStore.data.map { it[SESSION_TOKEN] }
    fun getSessionToken(): String? = runBlocking { sessionToken.first() }

    suspend fun cleatTokens() {
        Log.d(TAG, "Clearing tokens")
        dataStore.edit { it.clear() }
    }
}
