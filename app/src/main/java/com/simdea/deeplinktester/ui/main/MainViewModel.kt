package com.simdea.deeplinktester.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.preferences.ThemeOption
import com.simdea.deeplinktester.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val themeOption = userPreferencesRepository.themeOptionFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeOption.SYSTEM
        )

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(UserPreferencesRepository(application)) as T
            }
        }
    }
}
