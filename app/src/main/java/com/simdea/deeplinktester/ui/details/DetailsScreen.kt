package com.simdea.deeplinktester.ui.details

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.data.Collection
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkWithCollections
import com.simdea.deeplinktester.ui.composables.AddToCollectionDialog
import com.simdea.deeplinktester.ui.composables.PrimaryButton
import com.simdea.deeplinktester.ui.composables.UriEditor
import com.simdea.deeplinktester.ui.tester.Parameter
import kotlinx.coroutines.launch
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    deeplinkWithCollections: DeeplinkWithCollections,
    allCollections: List<Collection>,
    onNavigateUp: () -> Unit,
    onSaveAndLaunch: (Deeplink, Boolean) -> Unit,
    onDelete: (Deeplink) -> Unit,
    onToggleFavorite: (Deeplink) -> Unit,
    onAddCollectionAndGetId: suspend (String) -> Long,
    onAddDeeplinkToCollection: (Int, Int) -> Unit,
    onRemoveDeeplinkFromCollection: (Int, Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    onDataChanged: () -> Unit
) {
    val deeplink = deeplinkWithCollections.deeplink
    var title by remember { mutableStateOf(deeplink.title) }
    var fullUri by remember { mutableStateOf(deeplink.deeplink) }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showAddToCollectionDialog) {
        AddToCollectionDialog(
            allCollections = allCollections,
            onDismiss = { showAddToCollectionDialog = false },
            onConfirm = { selectedCollectionId, newCollectionName ->
                scope.launch {
                    if (newCollectionName != null && newCollectionName.isNotBlank()) {
                        val newCollectionId = onAddCollectionAndGetId(newCollectionName)
                        onAddDeeplinkToCollection(deeplink.id, newCollectionId.toInt())
                        snackbarHostState.showSnackbar("Added to new collection '$newCollectionName'")
                    } else if (selectedCollectionId != null) {
                        onAddDeeplinkToCollection(deeplink.id, selectedCollectionId)
                        val collectionName = allCollections.find { it.collectionId == selectedCollectionId }?.name
                        snackbarHostState.showSnackbar("Added to collection '$collectionName'")
                    }
                    showAddToCollectionDialog = false
                    onDataChanged()
                }
            }
        )
    }

    LaunchedEffect(deeplink) {
        title = deeplink.title
        fullUri = deeplink.deeplink
    }

    val submit = { shouldLaunch: Boolean ->
        var isError = false
        try {
            if (fullUri.isNotBlank()) {
                URI(fullUri)
            }
        } catch (e: Exception) {
            isError = true
        }

        if (fullUri.isNotBlank() && !isError) {
            val updatedDeeplink = deeplink.copy(title = title, deeplink = fullUri)
            onSaveAndLaunch(updatedDeeplink, shouldLaunch)
            onDataChanged()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deeplink Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        submit(false)
                        scope.launch {
                            snackbarHostState.showSnackbar("Deeplink saved")
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    IconButton(onClick = {
                        onToggleFavorite(deeplink)
                        onDataChanged()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (deeplink.isFavorite) "Removed from favorites" else "Added to favorites"
                            )
                        }
                    }) {
                        Icon(
                            if (deeplink.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Favorite"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title (Optional)") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            UriEditor(
                initialUri = fullUri,
                onUriChanged = { fullUri = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Collections",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = { showAddToCollectionDialog = true }) {
                    Text("+ Add to Collection")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                deeplinkWithCollections.collections.forEach { collection ->
                    InputChip(
                        selected = false,
                        onClick = { /* Non-interactive, for display only */ },
                        label = { Text(collection.name) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    onRemoveDeeplinkFromCollection(deeplink.id, collection.collectionId)
                                    onDataChanged()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed from '${collection.name}'")
                                    }
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove from collection")
                            }
                        }
                    )
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        onDelete(deeplink)
                        scope.launch {
                            snackbarHostState.showSnackbar("Deeplink deleted")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
                PrimaryButton(
                    text = "Save & Launch",
                    onClick = { submit(true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
