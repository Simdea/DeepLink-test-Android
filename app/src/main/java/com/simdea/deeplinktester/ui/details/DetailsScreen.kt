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
import com.simdea.deeplinktester.ui.tester.Parameter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    deeplinkWithCollections: DeeplinkWithCollections,
    allCollections: List<Collection>,
    onNavigateUp: () -> Unit,
    onLaunch: (Deeplink) -> Unit,
    onDelete: (Deeplink) -> Unit,
    onToggleFavorite: (Deeplink) -> Unit,
    onAddCollection: (String) -> Unit,
    onAddDeeplinkToCollection: (Int, Int) -> Unit,
    onRemoveDeeplinkFromCollection: (Int, Int) -> Unit
) {
    val deeplink = deeplinkWithCollections.deeplink
    var text by remember { mutableStateOf(deeplink.deeplink) }
    var isError by remember { mutableStateOf(false) }
    val parameters = remember { mutableStateListOf<Parameter>() }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }

    if (showAddToCollectionDialog) {
        val deeplinkCollectionIds = remember(deeplinkWithCollections) {
            deeplinkWithCollections.collections.map { it.collectionId }.toSet()
        }
        AddToCollectionDialog(
            allCollections = allCollections,
            deeplinkCollectionIds = deeplinkCollectionIds,
            onDismiss = { showAddToCollectionDialog = false },
            onConfirm = { newSelectedIds, newCollectionName ->
                // Handle creating a new collection first
                if (newCollectionName != null) {
                    onAddCollection(newCollectionName)
                }

                // Handle changes in selections
                val addedIds = newSelectedIds - deeplinkCollectionIds
                val removedIds = deeplinkCollectionIds - newSelectedIds

                addedIds.forEach { onAddDeeplinkToCollection(deeplink.id, it) }
                removedIds.forEach { onRemoveDeeplinkFromCollection(deeplink.id, it) }

                showAddToCollectionDialog = false
            }
        )
    }

    fun validate(input: String) {
        isError = try {
            if (input.isBlank()) false else {
                java.net.URI(input)
                false
            }
        } catch (e: Exception) {
            true
        }
    }

    LaunchedEffect(deeplink) {
        val uri = Uri.parse(deeplink.deeplink)
        text = uri.buildUpon().clearQuery().build().toString()
        parameters.clear()
        uri.queryParameterNames.forEach { key ->
            parameters.add(Parameter(key, uri.getQueryParameter(key) ?: ""))
        }
    }

    val submit = {
        validate(text)
        if (text.isNotBlank() && !isError) {
            val uriBuilder = Uri.parse(text).buildUpon()
            parameters.forEach { param ->
                if (param.key.isNotBlank()) {
                    uriBuilder.appendQueryParameter(param.key, param.value)
                }
            }
            onLaunch(deeplink.copy(deeplink = uriBuilder.build().toString()))
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
                    IconButton(onClick = { onToggleFavorite(deeplink) }) {
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
                value = text,
                onValueChange = {
                    text = it
                    validate(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("URI") },
                isError = isError,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Parameters",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { parameters.add(Parameter("", "")) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Parameter")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(parameters) { index, param ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = param.key,
                            onValueChange = { parameters[index] = param.copy(key = it) },
                            label = { Text("Key") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = param.value,
                            onValueChange = { parameters[index] = param.copy(value = it) },
                            label = { Text("Value") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { parameters.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Parameter", tint = Color.Red)
                        }
                    }
                }
            }

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
                    AssistChip(
                        onClick = { /* Non-interactive, for display only */ },
                        label = { Text(collection.name) }
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
                    onClick = { onDelete(deeplink) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
                PrimaryButton(
                    text = "Launch",
                    onClick = submit,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
