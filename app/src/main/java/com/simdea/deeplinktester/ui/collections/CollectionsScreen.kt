package com.simdea.deeplinktester.ui.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.CollectionWithDeeplinkCount
import com.simdea.deeplinktester.ui.composables.NewCollectionDialog
import com.simdea.deeplinktester.ui.composables.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    collections: List<CollectionWithDeeplinkCount>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onAddCollection: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NewCollectionDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                onAddCollection(it)
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collections") }
            )
        },
        floatingActionButton = {
            PrimaryButton(
                text = "+ New Collection",
                onClick = { showDialog = true }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search collections") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = collections,
                    key = { it.collection.collectionId }
                ) { collection ->
                    CollectionItem(collection = collection)
                }
            }
        }
    }
}

@Composable
fun CollectionItem(collection: CollectionWithDeeplinkCount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Collection",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = collection.collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${collection.deeplinkCount} links",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = { /* TODO: Implement more options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    }
}
