package com.simdea.deeplinktester.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeOption {
    LIGHT, DARK, SYSTEM
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_OPTION = stringPreferencesKey("theme_option")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val themeOptionFlow: Flow<ThemeOption> = context.dataStore.data
        .map { preferences ->
            ThemeOption.valueOf(
                preferences[PreferencesKeys.THEME_OPTION] ?: ThemeOption.SYSTEM.name
            )
        }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun updateThemeOption(themeOption: ThemeOption) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_OPTION] = themeOption.name
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
