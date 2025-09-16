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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionDialog(
    allCollections: List<Collection>,
    onDismiss: () -> Unit,
    onConfirm: (Int?, String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCollection by remember { mutableStateOf<Collection?>(null) }
    var newCollectionName by remember { mutableStateOf("") }
    var showNewCollectionField by remember { mutableStateOf(false) }

    val collectionsWithOptions = allCollections + listOf(Collection(collectionId = -1, name = "Create new collection..."))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Collection") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCollection?.name ?: if (showNewCollectionField) "Create new collection..." else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select a collection") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        collectionsWithOptions.forEach { collection ->
                            DropdownMenuItem(
                                text = { Text(collection.name) },
                                onClick = {
                                    if (collection.collectionId == -1) {
                                        showNewCollectionField = true
                                        selectedCollection = null
                                    } else {
                                        showNewCollectionField = false
                                        selectedCollection = collection
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                if (showNewCollectionField) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("New collection name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (showNewCollectionField) {
                        onConfirm(null, newCollectionName)
                    } else {
                        onConfirm(selectedCollection?.collectionId, null)
                    }
                },
                enabled = (showNewCollectionField && newCollectionName.isNotBlank()) || (!showNewCollectionField && selectedCollection != null)
            ) {
                Text("Add")
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
