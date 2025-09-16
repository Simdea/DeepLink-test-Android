package com.simdea.deeplinktester.ui.tester

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.ui.composables.PrimaryButton
import java.net.URI

data class Parameter(var key: String, var value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TesterScreen(
    initialDeeplink: String?,
    onLaunch: (Deeplink) -> Unit,
    onScanQrCode: () -> Unit
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
                baseUri // If baseUri is invalid, just return it
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

    LaunchedEffect(initialDeeplink) {
        initialDeeplink?.let {
            try {
                val uri = Uri.parse(it)
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
                baseUri = it
                validate(it)
            }
        }
    }

    val submit = {
        validate(fullUri)
        if (fullUri.isNotBlank() && !isError) {
            val finalUri = Uri.parse(fullUri)
            val title = finalUri.host ?: "Untitled"
            onLaunch(Deeplink(title = title, deeplink = finalUri.toString()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Row {
                        IconButton(
                            onClick = { parametersVisible = !parametersVisible },
                            enabled = !isError && baseUri.isNotBlank()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Parameters")
                        }
                        IconButton(onClick = onScanQrCode) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
                        }
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
                    text = stringResource(id = R.string.invalid_uri_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(text = "Launch Deeplink", onClick = submit)

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
                        modifier = Modifier.fillMaxSize(),
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
}
