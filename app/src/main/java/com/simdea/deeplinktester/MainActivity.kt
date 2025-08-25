package com.simdea.deeplinktester

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.ui.history.HistoryScreen
import com.simdea.deeplinktester.ui.history.HistoryViewModel
import com.simdea.deeplinktester.ui.theme.DeepLinkTestAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeepLinkTestAndroidTheme {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.HistoryViewModelFactory(
            context.applicationContext as Application
        )
    )

    Scaffold(
        bottomBar = { com.simdea.deeplinktester.ui.ads.BannerAd() }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("main") {
                MainScreen(
                    onNavigateToHistory = { navController.navigate("history") },
                    onLaunch = { deeplink ->
                        historyViewModel.addDeeplink(deeplink)
                        val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(deeplink.deeplink)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(launchBrowser)
                    }
                )
            }
            composable("history") {
                val history by historyViewModel.history.collectAsState()
                HistoryScreen(
                    history = history,
                    onRetry = { deeplink ->
                        val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(deeplink.deeplink)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(launchBrowser)
                    },
                    onRemove = { historyViewModel.removeDeeplink(it) }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToHistory: () -> Unit,
    onLaunch: (Deeplink) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.deeplink_label)) },
            modifier = Modifier.width(300.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (text.isNotBlank()) {
                onLaunch(Deeplink(deeplink = text))
            }
        }) {
            Text(stringResource(R.string.open_deeplink_button))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToHistory) {
            Text(stringResource(R.string.history_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DeepLinkTestAndroidTheme {
        MainScreen(onNavigateToHistory = {}, onLaunch = {})
    }
}
