package com.simdea.deeplinktester.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.Deeplink

@Composable
fun HistoryScreen(
    history: List<Deeplink>,
    onRetry: (Deeplink) -> Unit,
    onRemove: (Deeplink) -> Unit
) {
    var showDialog by remember { mutableStateOf<Deeplink?>(null) }

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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(history) { deeplink ->
            HistoryItem(
                deeplink = deeplink,
                onRetry = { onRetry(deeplink) },
                onRemove = { showDialog = deeplink }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HistoryItem(
    deeplink: Deeplink,
    onRetry: () -> Unit,
    onRemove: () -> Unit
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
            Text(
                text = deeplink.deeplink,
                modifier = Modifier.weight(1f)
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
