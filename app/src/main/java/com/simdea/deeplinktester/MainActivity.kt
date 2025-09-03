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
import com.simdea.deeplinktester.ui.editor.ParameterEditorScreen
import com.simdea.deeplinktester.ui.history.HistoryScreen
import com.simdea.deeplinktester.data.preferences.ThemeOption
import com.simdea.deeplinktester.ui.history.HistoryViewModel
import com.simdea.deeplinktester.ui.scanner.QrCodeScannerScreen
import androidx.compose.foundation.isSystemInDarkTheme
import com.simdea.deeplinktester.ui.main.MainViewModel
import com.simdea.deeplinktester.ui.settings.SettingsScreen
import com.simdea.deeplinktester.ui.settings.SettingsViewModel
import com.simdea.deeplinktester.ui.theme.DeepLinkTestAndroidTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URLEncoder

sealed class Screen(val route: String, val resourceId: Int, val icon: @Composable () -> Unit) {
    object Main : Screen("main", R.string.main_screen_title, { Icon(Icons.Filled.Home, contentDescription = null) })
    object History : Screen("history", R.string.history_screen_title, { Icon(Icons.Filled.History, contentDescription = null) })
    object Settings : Screen("settings", R.string.settings_screen_title, { Icon(Icons.Filled.Settings, contentDescription = null) })
    object QrScanner : Screen("qrScanner", R.string.qr_scanner_title, { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) })
    object ParameterEditor : Screen("parameterEditor", R.string.parameter_editor_title, { Icon(Icons.Filled.Edit, contentDescription = null) })
}

val items = listOf(
    Screen.Main,
    Screen.History,
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

    val useDarkTheme = when (themeOption) {
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    DeepLinkTestAndroidTheme(darkTheme = useDarkTheme) {
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
                    val currentDeeplinks = currentHistory.map { it.deeplink }.toSet()
                    val newDeeplinks = importedHistory.filter { !currentDeeplinks.contains(it.deeplink) }
                    newDeeplinks.forEach { historyViewModel.addDeeplink(it) }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            composable(
                route = "${Screen.Main.route}?deeplink={deeplink}",
                arguments = listOf(navArgument("deeplink") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                MainScreen(
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
                    onScanQrCode = { navController.navigate(Screen.QrScanner.route) },
                    onEditParameters = { deeplink ->
                        val encodedDeeplink = URLEncoder.encode(deeplink, "UTF-8")
                        navController.navigate("${Screen.ParameterEditor.route}?deeplink=$encodedDeeplink")
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
            composable(
                route = "${Screen.ParameterEditor.route}?deeplink={deeplink}",
                arguments = listOf(navArgument("deeplink") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                ParameterEditorScreen(
                    deeplink = backStackEntry.arguments?.getString("deeplink") ?: "",
                    onApply = { editedDeeplink ->
                        val encodedDeeplink = URLEncoder.encode(editedDeeplink, "UTF-8")
                        navController.navigate("${Screen.Main.route}?deeplink=$encodedDeeplink") {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    initialDeeplink: String?,
    onLaunch: (Deeplink) -> Unit,
    onScanQrCode: () -> Unit,
    onEditParameters: (String) -> Unit
) {
    var text by remember(initialDeeplink) { mutableStateOf(initialDeeplink ?: "") }
    var isError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

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

    val submit = {
        validate(text)
        if (text.isNotBlank() && !isError) {
            onLaunch(Deeplink(deeplink = text))
            keyboardController?.hide()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    validate(it)
                },
                label = { Text(stringResource(R.string.deeplink_label)) },
                modifier = Modifier.width(300.dp),
                singleLine = true,
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(stringResource(R.string.invalid_uri_error))
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
            IconButton(onClick = onScanQrCode) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan QR Code")
            }
            IconButton(
                onClick = { onEditParameters(text) },
                enabled = !isError && text.isNotBlank()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Parameters")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = submit) {
            Text(stringResource(R.string.open_deeplink_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DeepLinkTestAndroidTheme {
        MainScreen(initialDeeplink = null, onLaunch = {}, onScanQrCode = {}, onEditParameters = {})
    }
}
