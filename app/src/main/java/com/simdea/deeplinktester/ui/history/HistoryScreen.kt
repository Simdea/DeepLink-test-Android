package com.simdea.deeplinktester.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.Collection
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkWithCollections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<DeeplinkWithCollections>,
    collections: List<Collection>,
    showOnlyFavorites: Boolean,
    searchQuery: String,
    selectedCollectionId: Int?,
    onItemClick: (Int) -> Unit,
    onToggleShowOnlyFavorites: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCollectionSelected: (Int?) -> Unit,
    onRetry: (Deeplink) -> Unit,
    onRemove: (Deeplink) -> Unit,
    onToggleFavorite: (Deeplink) -> Unit,
    onAddCollection: (String) -> Unit,
    onAddDeeplinkToCollection: (Int, Int) -> Unit,
    onRemoveDeeplinkFromCollection: (Int, Int) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History & Favorites") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search history...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Favorites")
                Switch(
                    checked = showOnlyFavorites,
                    onCheckedChange = { onToggleShowOnlyFavorites() },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No History",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your tested deeplinks will appear here!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = history,
                        key = { item -> item.deeplink.id }
                    ) { item ->
                        HistoryItem(
                            item = item,
                            onClick = { onItemClick(item.deeplink.id) },
                            onRetry = { onRetry(item.deeplink) },
                            onRemove = {
                                onRemove(item.deeplink)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Deeplink removed from history")
                                }
                            },
                            onToggleFavorite = {
                                onToggleFavorite(item.deeplink)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (item.deeplink.isFavorite) "Removed from favorites" else "Added to favorites"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
