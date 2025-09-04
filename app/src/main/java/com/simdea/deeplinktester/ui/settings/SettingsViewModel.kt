package com.simdea.deeplinktester.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.preferences.ThemeOption
import com.simdea.deeplinktester.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val themeOptionFlow = userPreferencesRepository.themeOptionFlow

    fun updateThemeOption(themeOption: ThemeOption) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeOption(themeOption)
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(UserPreferencesRepository(application)) as T
            }
        }
    }
}
