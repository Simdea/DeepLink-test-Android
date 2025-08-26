package com.simdea.deeplinktester.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.AppDatabase
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val deeplinkDao: DeeplinkDao) : ViewModel() {

    val history: StateFlow<List<Deeplink>> = deeplinkDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDeeplink(deeplink: Deeplink) {
        viewModelScope.launch {
            deeplinkDao.insert(deeplink)
        }
    }

    fun removeDeeplink(deeplink: Deeplink) {
        viewModelScope.launch {
            deeplinkDao.delete(deeplink)
        }
    }

    class HistoryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(AppDatabase.getDatabase(application).deeplinkDao()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
