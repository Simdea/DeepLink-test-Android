package com.simdea.deeplinktester.ui.collectiondetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkWithCollections
import com.simdea.deeplinktester.ui.history.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    collectionName: String,
    deeplinks: List<DeeplinkWithCollections>,
    onNavigateUp: () -> Unit,
    onItemClick: (Int) -> Unit,
    onRetry: (Deeplink) -> Unit,
    onRemove: (Deeplink) -> Unit,
    onToggleFavorite: (Deeplink) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collectionName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = deeplinks,
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
