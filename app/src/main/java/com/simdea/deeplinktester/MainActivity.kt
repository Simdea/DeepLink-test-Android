package com.simdea.deeplinktester

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.gms.ads.MobileAds
import com.simdea.deeplinktester.data.Deeplink
import com.simdea.deeplinktester.data.DeeplinkWithCollections
import com.simdea.deeplinktester.ui.ads.BannerAd
import com.simdea.deeplinktester.ui.history.HistoryScreen
import com.simdea.deeplinktester.data.preferences.ThemeOption
import com.simdea.deeplinktester.ui.history.HistoryViewModel
import com.simdea.deeplinktester.ui.scanner.QrCodeScannerScreen
import androidx.compose.foundation.isSystemInDarkTheme
import com.simdea.deeplinktester.ui.collections.CollectionsScreen
import com.simdea.deeplinktester.ui.main.MainViewModel
import com.simdea.deeplinktester.ui.tester.TesterScreen
import com.simdea.deeplinktester.ui.onboarding.OnboardingScreen
import com.simdea.deeplinktester.ui.onboarding.OnboardingViewModel
import com.simdea.deeplinktester.ui.settings.SettingsScreen
import com.simdea.deeplinktester.ui.details.DetailsScreen
import com.simdea.deeplinktester.ui.settings.SettingsViewModel
import com.simdea.deeplinktester.ui.theme.DeepLinkTestAndroidTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URLEncoder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Science

sealed class Screen(val route: String, val resourceId: Int, val icon: @Composable () -> Unit) {
    object Splash : Screen("splash", 0, { /* No icon */ })
    object Main : Screen("main", R.string.main_screen_title, { Icon(Icons.Outlined.Science, contentDescription = null) })
    object History : Screen("history", R.string.history_screen_title, { Icon(Icons.Outlined.History, contentDescription = null) })
    object Collections : Screen("collections", R.string.collections_screen_title, { Icon(Icons.Outlined.CollectionsBookmark, contentDescription = null) })
    object Settings : Screen("settings", R.string.settings_screen_title, { Icon(Icons.Outlined.Settings, contentDescription = null) })
    object QrScanner : Screen("qrScanner", R.string.qr_scanner_title, { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) })
    object Onboarding : Screen("onboarding", R.string.onboarding_screen_title, { Icon(Icons.Filled.Info, contentDescription = null) })
    object Details : Screen("details/{deeplinkId}", R.string.details_screen_title, { Icon(Icons.Filled.Info, contentDescription = null) }) {
        fun createRoute(deeplinkId: Int) = "details/$deeplinkId"
    }
}

val items = listOf(
    Screen.Main,
    Screen.History,
    Screen.Collections,
    Screen.Settings
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            AppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.provideFactory(context.applicationContext as Application)
    )
    val themeOption by mainViewModel.themeOption.collectAsState()
    val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsState()

    val useDarkTheme = when (themeOption) {
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    DeepLinkTestAndroidTheme(darkTheme = useDarkTheme) {
        if (onboardingCompleted == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val navController = rememberNavController()
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.HistoryViewModelFactory(
                    context.applicationContext as Application
                )
            )
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val gson = Gson()

            val exportLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/json")
            ) { uri ->
                uri?.let {
                    scope.launch {
                        val history = historyViewModel.history.first()
                        val json = gson.toJson(history)
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(json.toByteArray())
                        }
                    }
                }
            }

            val importLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let {
                    scope.launch {
                        context.contentResolver.openInputStream(it)?.use { inputStream ->
                            val reader = BufferedReader(InputStreamReader(inputStream))
                            val json = reader.readText()
                            val type = object : TypeToken<List<Deeplink>>() {}.type
                            val importedHistory: List<Deeplink> = gson.fromJson(json, type)
                            val currentHistory = historyViewModel.history.first()
                            val currentDeeplinks = currentHistory.map { it.deeplink.deeplink }.toSet()
                            val newDeeplinks = importedHistory.filter { !currentDeeplinks.contains(it.deeplink) }
                            newDeeplinks.forEach { historyViewModel.addDeeplink(it) }
                        }
                    }
                }
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = currentDestination?.hierarchy?.any { dest -> items.any { it.route == dest.route } } == true

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showBottomBar) {
                        Column {
                            BannerAd()
                            NavigationBar {
                                items.forEach { screen ->
                                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { screen.icon() },
                                        label = { Text(stringResource(screen.resourceId)) },
                                        selected = isSelected,
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
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Splash.route) {
                        if (onboardingCompleted != null) {
                            LaunchedEffect(onboardingCompleted) {
                                val route = if (onboardingCompleted == true) Screen.Main.route else Screen.Onboarding.route
                                navController.navigate(route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    composable(
                        route = "${Screen.Main.route}?deeplink={deeplink}",
                        arguments = listOf(navArgument("deeplink") {
                            type = NavType.StringType
                            nullable = true
                        })
                    ) { backStackEntry ->
                        TesterScreen(
                            initialDeeplink = backStackEntry.arguments?.getString("deeplink"),
                            onLaunch = { deeplink ->
                                historyViewModel.addDeeplink(deeplink)
                                try {
                                    val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(deeplink.deeplink)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(launchBrowser)
                                } catch (e: ActivityNotFoundException) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.activity_not_found_error),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            onScanQrCode = { navController.navigate(Screen.QrScanner.route) }
                        )
                    }
                    composable(Screen.Onboarding.route) {
                        val onboardingViewModel: OnboardingViewModel = viewModel(
                            factory = OnboardingViewModel.provideFactory(
                                context.applicationContext as Application
                            )
                        )
                        OnboardingScreen(
                            onOnboardingCompleted = {
                                onboardingViewModel.completeOnboarding()
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.History.route) {
                        val history by historyViewModel.history.collectAsState()
                        val collections by historyViewModel.collections.collectAsState()
                        val showOnlyFavorites by historyViewModel.showOnlyFavorites.collectAsState()
                        val searchQuery by historyViewModel.searchQuery.collectAsState()
                        val selectedCollectionId by historyViewModel.selectedCollectionId.collectAsState()
                        HistoryScreen(
                            history = history,
                            collections = collections,
                            showOnlyFavorites = showOnlyFavorites,
                            searchQuery = searchQuery,
                            selectedCollectionId = selectedCollectionId,
                            onItemClick = { deeplinkId ->
                                navController.navigate(Screen.Details.createRoute(deeplinkId))
                            },
                            onToggleShowOnlyFavorites = { historyViewModel.toggleShowOnlyFavorites() },
                            onSearchQueryChanged = { historyViewModel.onSearchQueryChanged(it) },
                            onCollectionSelected = { historyViewModel.onCollectionSelected(it) },
                            onRetry = { deeplink ->
                                try {
                                    val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(deeplink.deeplink)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(launchBrowser)
                                } catch (e: ActivityNotFoundException) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.activity_not_found_error),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            onRemove = { historyViewModel.removeDeeplink(it) },
                            onToggleFavorite = { historyViewModel.toggleFavorite(it) },
                            onAddCollection = { historyViewModel.addCollection(it) },
                            onAddDeeplinkToCollection = { deeplinkId, collectionId ->
                                historyViewModel.addDeeplinkToCollection(deeplinkId, collectionId)
                            },
                            onRemoveDeeplinkFromCollection = { deeplinkId, collectionId ->
                                historyViewModel.removeDeeplinkFromCollection(deeplinkId, collectionId)
                            }
                        )
                    }
                    composable(Screen.Collections.route) {
                        val collections by historyViewModel.collectionsWithDeeplinkCount.collectAsState()
                        val searchQuery by historyViewModel.collectionSearchQuery.collectAsState()
                        CollectionsScreen(
                            collections = collections,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { historyViewModel.onCollectionSearchQueryChanged(it) },
                            onAddCollection = { historyViewModel.addCollection(it) }
                        )
                    }
                    composable(Screen.Settings.route) {
                        val settingsViewModel: SettingsViewModel = viewModel(
                            factory = SettingsViewModel.provideFactory(context.applicationContext as Application)
                        )
                        val themeOption by settingsViewModel.themeOptionFlow.collectAsState(initial = ThemeOption.SYSTEM)
                        SettingsScreen(
                            onExport = { exportLauncher.launch("deeplink_history.json") },
                            onImport = { importLauncher.launch(arrayOf("*/*")) },
                            themeOption = themeOption,
                            onThemeOptionSelected = { settingsViewModel.updateThemeOption(it) }
                        )
                    }
                    composable(
                        route = Screen.Details.route,
                        arguments = listOf(navArgument("deeplinkId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val deeplinkId = backStackEntry.arguments?.getInt("deeplinkId")
                        val deeplink = historyViewModel.getDeeplinkById(deeplinkId)
                        if (deeplink != null) {
                            DetailsScreen(
                                deeplink = deeplink,
                                onNavigateUp = { navController.navigateUp() },
                                onLaunch = { updatedDeeplink ->
                                    historyViewModel.updateDeeplink(updatedDeeplink)
                                    try {
                                        val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(updatedDeeplink.deeplink)
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(launchBrowser)
                                    } catch (e: ActivityNotFoundException) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = context.getString(R.string.activity_not_found_error),
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                onDelete = {
                                    historyViewModel.removeDeeplink(it)
                                    navController.navigateUp()
                                },
                                onToggleFavorite = { historyViewModel.toggleFavorite(it) }
                            )
                        }
                    }
                    composable(Screen.QrScanner.route) {
                        QrCodeScannerScreen(
                            onQrCodeScanned = { deeplink ->
                                val encodedDeeplink = URLEncoder.encode(deeplink, "UTF-8")
                                navController.navigate("${Screen.Main.route}?deeplink=$encodedDeeplink") {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

