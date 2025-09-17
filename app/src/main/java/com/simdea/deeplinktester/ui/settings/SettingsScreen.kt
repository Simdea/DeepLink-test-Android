package com.simdea.deeplinktester.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.sharp.Download
import androidx.compose.material.icons.sharp.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.preferences.ThemeOption
import kotlin.text.replaceFirstChar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onExport: () -> Unit,
    onImport: () -> Unit,
    themeOption: ThemeOption,
    onThemeOptionSelected: (ThemeOption) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "APPEARANCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card {
                Column {
                    ThemeOption.entries.forEach { option ->
                        ThemeSettingItem(
                            text = option.name.lowercase().replaceFirstChar(Char::titlecase),
                            selected = themeOption == option,
                            onClick = { onThemeOptionSelected(option) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "DATA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card {
                Column {
                    DataSettingItem(
                        text = "Export History",
                        icon = Icons.Sharp.Download,
                        onClick = onExport
                    )
                    Divider()
                    DataSettingItem(
                        text = "Import History",
                        icon = Icons.Sharp.Upload,
                        onClick = onImport
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSettingItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
private fun DataSettingItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}
