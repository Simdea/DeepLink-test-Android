package com.simdea.deeplinktester.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.Collection

@Composable
fun NewCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Collection") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Collection name") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddToCollectionDialog(
    allCollections: List<Collection>,
    deeplinkCollectionIds: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Set<Int>, String?) -> Unit
) {
    var newCollectionName by remember { mutableStateOf("") }
    val selectedCollectionIds = remember { mutableStateOf(deeplinkCollectionIds) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Collection") },
        text = {
            Column {
                Text("Create a new collection:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("New collection name") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            onConfirm(selectedCollectionIds.value, newCollectionName)
                            newCollectionName = ""
                        },
                        enabled = newCollectionName.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Collection")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Or select existing collections:")
                LazyColumn {
                    items(allCollections) { collection ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedCollectionIds.value.contains(collection.collectionId),
                                onCheckedChange = { isChecked ->
                                    val currentIds = selectedCollectionIds.value.toMutableSet()
                                    if (isChecked) {
                                        currentIds.add(collection.collectionId)
                                    } else {
                                        currentIds.remove(collection.collectionId)
                                    }
                                    selectedCollectionIds.value = currentIds
                                }
                            )
                            Text(collection.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedCollectionIds.value, null) }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditCollectionDialog(
    collection: Collection,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(collection.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Collection") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Collection name") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteCollectionDialog(
    collection: Collection,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Collection") },
        text = { Text("Are you sure you want to delete the collection \"${collection.name}\"? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = { onConfirm() }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
