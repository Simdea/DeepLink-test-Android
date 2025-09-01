package com.simdea.deeplinktester.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ParameterEditorScreen(
    deeplink: String,
    onApply: (String) -> Unit
) {
    val viewModel: ParameterEditorViewModel = viewModel()
    val parameters by viewModel.parameters.collectAsState()

    LaunchedEffect(deeplink) {
        viewModel.setDeeplink(deeplink)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(parameters) { index, (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = key,
                        onValueChange = { viewModel.updateParameter(index, it, value) },
                        label = { Text("Key") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { viewModel.updateParameter(index, key, it) },
                        label = { Text("Value") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.removeParameter(index) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove Parameter")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.addParameter() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Parameter")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Parameter")
            }
            Button(onClick = { onApply(viewModel.buildDeeplink()) }) {
                Text("Apply")
            }
        }
    }
}
