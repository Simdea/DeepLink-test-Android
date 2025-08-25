package com.simdea.deeplinktester

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.ui.ads.BannerAd
import com.simdea.deeplinktester.ui.history.HistoryScreen
import com.simdea.deeplinktester.ui.history.HistoryViewModel
import com.simdea.deeplinktester.ui.theme.DeepLinkTestAndroidTheme

sealed class Screen(val route: String, val resourceId: Int, val icon: @Composable () -> Unit) {
    object Main : Screen("main", R.string.main_screen_title, { Icon(Icons.Filled.Home, contentDescription = null) })
    object History : Screen("history", R.string.history_screen_title, { Icon(Icons.Filled.History, contentDescription = null) })
}

val items = listOf(
    Screen.Main,
    Screen.History
)

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
        bottomBar = {
            Column {
                BannerAd()
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon() },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Main.route, Modifier.padding(innerPadding)) {
            composable(Screen.Main.route) {
                MainScreen(
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
            composable(Screen.History.route) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DeepLinkTestAndroidTheme {
        MainScreen(onLaunch = {})
    }
}
