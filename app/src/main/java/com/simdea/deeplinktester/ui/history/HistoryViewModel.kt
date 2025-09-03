package com.simdea.deeplinktester.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.AppDatabase
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val deeplinkDao: DeeplinkDao) : ViewModel() {

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val history: StateFlow<List<Deeplink>> = combine(
        deeplinkDao.getAll(),
        _showOnlyFavorites,
        _searchQuery
    ) { deeplinks, onlyFavorites, query ->
        val filteredByFavorites = if (onlyFavorites) {
            deeplinks.filter { it.isFavorite }
        } else {
            deeplinks
        }
        if (query.isBlank()) {
            filteredByFavorites
        } else {
            filteredByFavorites.filter { it.deeplink.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowOnlyFavorites() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

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

    fun toggleFavorite(deeplink: Deeplink) {
        viewModelScope.launch {
            deeplinkDao.update(deeplink.copy(isFavorite = !deeplink.isFavorite))
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
