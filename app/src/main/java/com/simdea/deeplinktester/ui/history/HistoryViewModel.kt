package com.simdea.deeplinktester.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val deeplinkDao: DeeplinkDao) : ViewModel() {

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCollectionId = MutableStateFlow<Int?>(null)
    val selectedCollectionId: StateFlow<Int?> = _selectedCollectionId.asStateFlow()

    val history: StateFlow<List<DeeplinkWithCollections>> = combine(
        deeplinkDao.getAll(),
        _showOnlyFavorites,
        _searchQuery,
        _selectedCollectionId
    ) { deeplinks, onlyFavorites, query, collectionId ->
        val filteredByFavorites = if (onlyFavorites) {
            deeplinks.filter { it.deeplink.isFavorite }
        } else {
            deeplinks
        }
        val filteredBySearch = if (query.isBlank()) {
            filteredByFavorites
        } else {
            filteredByFavorites.filter { it.deeplink.deeplink.contains(query, ignoreCase = true) }
        }
        if (collectionId == null) {
            filteredBySearch
        } else {
            filteredBySearch.filter { it.collections.any { c -> c.collectionId == collectionId } }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCollectionSelected(collectionId: Int?) {
        _selectedCollectionId.value = collectionId
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

    val collections: StateFlow<List<Collection>> = deeplinkDao.getAllCollections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCollection(name: String) {
        viewModelScope.launch {
            deeplinkDao.insertCollection(Collection(name = name))
        }
    }

    fun addDeeplinkToCollection(deeplinkId: Int, collectionId: Int) {
        viewModelScope.launch {
            deeplinkDao.insertDeeplinkCollectionCrossRef(
                DeeplinkCollectionCrossRef(
                    deeplinkId = deeplinkId,
                    collectionId = collectionId
                )
            )
        }
    }

    fun removeDeeplinkFromCollection(deeplinkId: Int, collectionId: Int) {
        viewModelScope.launch {
            deeplinkDao.deleteDeeplinkCollectionCrossRef(
                DeeplinkCollectionCrossRef(
                    deeplinkId = deeplinkId,
                    collectionId = collectionId
                )
            )
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
