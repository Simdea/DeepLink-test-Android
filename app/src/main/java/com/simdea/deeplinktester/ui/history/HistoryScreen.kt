package com.simdea.deeplinktester.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.data.Collection
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkWithCollections
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<DeeplinkWithCollections>,
    collections: List<Collection>,
    showOnlyFavorites: Boolean,
    searchQuery: String,
    selectedCollectionId: Int?,
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
    var showRemoveDialog by remember { mutableStateOf<Deeplink?>(null) }
    var showCollectionDialog by remember { mutableStateOf<DeeplinkWithCollections?>(null) }
    var isCollectionDropdownExpanded by remember { mutableStateOf(false) }

    showRemoveDialog?.let { deeplinkToRemove ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text(stringResource(R.string.remove_deeplink_dialog_title)) },
            text = { Text(stringResource(R.string.remove_deeplink_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        onRemove(deeplinkToRemove)
                        showRemoveDialog = null
                    }
                ) {
                    Text(stringResource(R.string.remove_button))
                }
            },
            dismissButton = {
                Button(onClick = { showRemoveDialog = null }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    showCollectionDialog?.let { item ->
        CollectionManagementDialog(
            item = item,
            allCollections = collections,
            onDismiss = { showCollectionDialog = null },
            onAddCollection = onAddCollection,
            onAddDeeplinkToCollection = onAddDeeplinkToCollection,
            onRemoveDeeplinkFromCollection = onRemoveDeeplinkFromCollection
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )
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
                onCheckedChange = { onToggleShowOnlyFavorites() }
            )
        }

        ExposedDropdownMenuBox(
            expanded = isCollectionDropdownExpanded,
            onExpandedChange = { isCollectionDropdownExpanded = !isCollectionDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = collections.find { it.collectionId == selectedCollectionId }?.name ?: "All Collections",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Collection") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCollectionDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isCollectionDropdownExpanded,
                onDismissRequest = { isCollectionDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Collections") },
                    onClick = {
                        onCollectionSelected(null)
                        isCollectionDropdownExpanded = false
                    }
                )
                collections.forEach { collection ->
                    DropdownMenuItem(
                        text = { Text(collection.name) },
                        onClick = {
                            onCollectionSelected(collection.collectionId)
                            isCollectionDropdownExpanded = false
                        }
                    )
                }
            }
        }

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
                onCheckedChange = { onToggleShowOnlyFavorites() }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = history,
                key = { item -> item.deeplink.id }
            ) { item ->
                HistoryItem(
                    item = item,
                    onRetry = { onRetry(item.deeplink) },
                    onRemove = { onRemove(item.deeplink) },
                    onToggleFavorite = { onToggleFavorite(item.deeplink) },
                    onManageCollections = { showCollectionDialog = item },
                    onCollectionSelected = { onCollectionSelected(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryItem(
    item: DeeplinkWithCollections,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    onToggleFavorite: () -> Unit,
    onManageCollections: () -> Unit,
    onCollectionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (item.deeplink.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorite"
                    )
                }
                Text(
                    text = item.deeplink.deeplink,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onManageCollections) {
                        Icon(Icons.Default.Label, contentDescription = "Manage Collections")
                    }
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry_button))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onRemove) {
                        Text(stringResource(R.string.remove_button))
                    }
                }
            }
            if (item.collections.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.collections.forEach { collection ->
                        AssistChip(
                            onClick = { onCollectionSelected(collection.collectionId) },
                            label = { Text(collection.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionManagementDialog(
    item: DeeplinkWithCollections,
    allCollections: List<Collection>,
    onDismiss: () -> Unit,
    onAddCollection: (String) -> Unit,
    onAddDeeplinkToCollection: (Int, Int) -> Unit,
    onRemoveDeeplinkFromCollection: (Int, Int) -> Unit
) {
    val deeplinkCollectionIds = remember(item) { item.collections.map { it.collectionId }.toSet() }
    var newCollectionName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding()
            ) {
                Text("Manage Collections", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Add new collection UI
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("New collection name") },
                        modifier = Modifier.weight(1f),
                        keyboardActions = KeyboardActions(onDone = {
                            if (newCollectionName.isNotBlank()) {
                                onAddCollection(newCollectionName)
                                newCollectionName = ""
                            }
                        }),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (newCollectionName.isNotBlank()) {
                                onAddCollection(newCollectionName)
                                newCollectionName = ""
                            }
                        },
                        enabled = newCollectionName.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Collection")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                // List of existing collections
                if (allCollections.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No collections yet. Add one above.")
                    }
                } else {
                    LazyColumn {
                        items(allCollections) { collection ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = deeplinkCollectionIds.contains(collection.collectionId),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            onAddDeeplinkToCollection(item.deeplink.id, collection.collectionId)
                                        } else {
                                            onRemoveDeeplinkFromCollection(item.deeplink.id, collection.collectionId)
                                        }
                                    }
                                )
                                Text(collection.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }

                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Dialog action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
