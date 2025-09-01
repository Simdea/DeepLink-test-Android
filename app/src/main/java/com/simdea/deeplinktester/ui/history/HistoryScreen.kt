package com.simdea.deeplinktester.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.data.Deeplink

@Composable
fun HistoryScreen(
    history: List<Deeplink>,
    onRetry: (Deeplink) -> Unit,
    onRemove: (Deeplink) -> Unit,
    onToggleFavorite: (Deeplink) -> Unit
) {
    var showDialog by remember { mutableStateOf<Deeplink?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    showDialog?.let { deeplinkToRemove ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text(stringResource(R.string.remove_deeplink_dialog_title)) },
            text = { Text(stringResource(R.string.remove_deeplink_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        onRemove(deeplinkToRemove)
                        showDialog = null
                    }
                ) {
                    Text(stringResource(R.string.remove_button))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = null }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("Show only favorites")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = showOnlyFavorites,
                onCheckedChange = { showOnlyFavorites = it }
            )
        }

        val filteredHistory = if (showOnlyFavorites) {
            history.filter { it.isFavorite }
        } else {
            history
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredHistory) { deeplink ->
                HistoryItem(
                    deeplink = deeplink,
                    onRetry = { onRetry(deeplink) },
                    onRemove = { showDialog = deeplink },
                    onToggleFavorite = { onToggleFavorite(deeplink) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HistoryItem(
    deeplink: Deeplink,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (deeplink.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite"
                )
            }
            Text(
                text = deeplink.deeplink,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            Row {
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.retry_button))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRemove) {
                    Text(stringResource(R.string.remove_button))
                }
            }
        }
    }
}
