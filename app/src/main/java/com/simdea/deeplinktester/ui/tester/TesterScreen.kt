package com.simdea.deeplinktester.ui.tester

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    var text by remember(initialDeeplink) { mutableStateOf(initialDeeplink ?: "") }
    var isError by remember { mutableStateOf(false) }
    val parameters = remember { mutableStateListOf<Parameter>() }

    fun validate(input: String) {
        isError = try {
            if (input.isBlank()) false else {
                URI(input)
                false
            }
        } catch (e: Exception) {
            true
        }
    }

    LaunchedEffect(initialDeeplink) {
        initialDeeplink?.let {
            val uri = Uri.parse(it)
            text = uri.buildUpon().clearQuery().build().toString()
            parameters.clear()
            uri.queryParameterNames.forEach { key ->
                parameters.add(Parameter(key, uri.getQueryParameter(key) ?: ""))
            }
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
            val finalUri = uriBuilder.build()
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
                value = text,
                onValueChange = {
                    text = it
                    validate(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("URI") },
                placeholder = { Text("app://example.com/path") },
                trailingIcon = {
                    IconButton(onClick = onScanQrCode) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
                    }
                },
                isError = isError,
                singleLine = true
            )
            if (!isError && text.isNotBlank()) {
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
