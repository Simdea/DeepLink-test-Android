package com.simdea.deeplinktester.ui.composables

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.ui.tester.Parameter
import java.net.URI

@Composable
fun UriEditor(
    initialUri: String,
    onUriChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var baseUri by remember { mutableStateOf("") }
    val parameters = remember { mutableStateListOf<Parameter>() }
    var isError by remember { mutableStateOf(false) }
    var parametersVisible by remember { mutableStateOf(false) }

    val fullUri by remember {
        derivedStateOf {
            try {
                val builder = Uri.parse(baseUri).buildUpon()
                parameters.forEach { p ->
                    if (p.key.isNotBlank()) {
                        builder.appendQueryParameter(p.key, p.value)
                    }
                }
                builder.build().toString()
            } catch (e: Exception) {
                baseUri
            }
        }
    }

    fun validate(uri: String) {
        isError = try {
            if (uri.isBlank()) false else {
                URI(uri)
                false
            }
        } catch (e: Exception) {
            true
        }
    }

    LaunchedEffect(initialUri) {
        if (initialUri.isNotBlank()) {
            try {
                val uri = Uri.parse(initialUri)
                baseUri = uri.buildUpon().clearQuery().build().toString()
                parameters.clear()
                uri.queryParameterNames.forEach { key ->
                    parameters.add(Parameter(key, uri.getQueryParameter(key) ?: ""))
                }
                if (parameters.isNotEmpty()) {
                    parametersVisible = true
                }
                validate(baseUri)
            } catch (e: Exception) {
                baseUri = initialUri
                validate(initialUri)
            }
        }
    }

    LaunchedEffect(fullUri) {
        onUriChanged(fullUri)
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = fullUri,
            onValueChange = { newValue ->
                try {
                    val uri = Uri.parse(newValue)
                    baseUri = uri.buildUpon().clearQuery().build().toString()
                    parameters.clear()
                    uri.queryParameterNames.forEach { key ->
                        parameters.add(Parameter(key, uri.getQueryParameter(key) ?: ""))
                    }
                    validate(baseUri)
                } catch (e: Exception) {
                    baseUri = newValue
                    validate(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("URI") },
            placeholder = { Text("app://example.com/path") },
            trailingIcon = {
                IconButton(
                    onClick = { parametersVisible = !parametersVisible },
                    enabled = !isError && baseUri.isNotBlank()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Parameters")
                }
            },
            isError = isError,
            singleLine = true
        )
        if (!isError && baseUri.isNotBlank()) {
            Text(
                text = "Valid syntax",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        } else if (isError) {
            Text(
                text = "Invalid URI syntax",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        AnimatedVisibility(visible = parametersVisible) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "URI Parameters",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { parameters.add(Parameter("", "")) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Parameter")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }
}
