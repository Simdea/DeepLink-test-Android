package com.simdea.deeplinktester.ui.tester

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.ui.composables.PrimaryButton
import com.simdea.deeplinktester.ui.composables.UriEditor
import java.net.URI

data class Parameter(var key: String, var value: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TesterScreen(
    initialDeeplink: String?,
    onLaunch: (Deeplink) -> Unit,
    onScanQrCode: () -> Unit
) {
    var fullUri by remember { mutableStateOf(initialDeeplink ?: "") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val submit = {
        keyboardController?.hide()
        var isError = false
        try {
            if (fullUri.isNotBlank()) {
                URI(fullUri)
            }
        } catch (e: Exception) {
            isError = true
        }

        if (fullUri.isNotBlank() && !isError) {
            val finalUri = Uri.parse(fullUri)
            val title = finalUri.host ?: "Untitled"
            onLaunch(Deeplink(title = title, deeplink = finalUri.toString()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onScanQrCode) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
                    }
                }
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
            UriEditor(
                initialUri = fullUri,
                onUriChanged = { fullUri = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = "Launch Deeplink",
                onClick = submit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
