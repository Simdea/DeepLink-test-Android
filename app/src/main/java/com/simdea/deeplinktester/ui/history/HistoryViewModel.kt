package com.simdea.deeplinktester.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simdea.deeplinktester.data.AppDatabase
import com.simdea.deeplinktester.data.Collection
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkCollectionCrossRef
import com.simdea.deeplinktester.data.DeeplinkDao
import com.simdea.deeplinktester.data.DeeplinkWithCollections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import com.simdea.deeplinktester.data.CollectionWithDeeplinkCount
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val deeplinkDao: DeeplinkDao) : ViewModel() {

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _collectionSearchQuery = MutableStateFlow("")
    val collectionSearchQuery: StateFlow<String> = _collectionSearchQuery.asStateFlow()

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

    val deeplinksInSelectedCollection: StateFlow<List<DeeplinkWithCollections>> = combine(
        history,
        _selectedCollectionId
    ) { history, collectionId ->
        if (collectionId == null) {
            emptyList()
        } else {
            history.filter { it.collections.any { c -> c.collectionId == collectionId } }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCollectionSearchQueryChanged(query: String) {
        _collectionSearchQuery.value = query
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

    fun getDeeplinkById(id: Int?): Deeplink? {
        return history.value.find { it.deeplink.id == id }?.deeplink
    }

    fun getDeeplinkWithCollectionsById(id: Int?): DeeplinkWithCollections? {
        return history.value.find { it.deeplink.id == id }
    }

    fun updateDeeplink(deeplink: Deeplink) {
        viewModelScope.launch {
            deeplinkDao.update(deeplink)
        }
    }

    val collections: StateFlow<List<Collection>> = deeplinkDao.getAllCollections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val collectionsWithDeeplinkCount: StateFlow<List<CollectionWithDeeplinkCount>> =
        combine(
            deeplinkDao.getCollectionsWithDeeplinkCount(),
            _collectionSearchQuery
        ) { collections, query ->
            if (query.isBlank()) {
                collections
            } else {
                collections.filter { it.collection.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(
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

    fun updateCollection(collection: Collection) {
        viewModelScope.launch {
            deeplinkDao.updateCollection(collection)
        }
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            deeplinkDao.deleteCollection(collection)
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
