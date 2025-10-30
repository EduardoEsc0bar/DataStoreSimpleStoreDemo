package edu.farmingdale.datastoresimplestoredemo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

class AppStorage(private val context: Context) {

    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("user_name")
        val HIGHSCORE = intPreferencesKey("high_score")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val appPreferenceFlow: Flow<AppPreferences> =
        context.dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map { prefs ->
                val userName  = prefs[PreferencesKeys.USERNAME] ?: ""
                val highScore = prefs[PreferencesKeys.HIGHSCORE] ?: 0
                val darkMode  = prefs[PreferencesKeys.DARK_MODE] ?: false
                AppPreferences(userName, highScore, darkMode)
            }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[PreferencesKeys.USERNAME] = username }
    }

    // For todo1, store a high score and a dark mode preference
    // Save High Score
    suspend fun saveHighScore(highScore: Int) {
        context.dataStore.edit { it[PreferencesKeys.HIGHSCORE] = highScore }
    }

    // Toggle/set dark mode
    suspend fun saveDarkMode(darkMode: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_MODE] = darkMode }
    }
}
