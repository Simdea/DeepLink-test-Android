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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    onRemoveDeeplinkFromCollection: (Int, Int) -> Unit
) {
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
                        onRemove = { onRemove(item.deeplink) },
                        onToggleFavorite = { onToggleFavorite(item.deeplink) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: DeeplinkWithCollections,
    onClick: () -> Unit,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "Deeplink",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.deeplink.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.deeplink.deeplink,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRemove) {
                        Text("Delete")
                    }
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (item.deeplink.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Favorite",
                    tint = if (item.deeplink.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
