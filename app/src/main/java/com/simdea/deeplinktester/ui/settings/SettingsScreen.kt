package com.simdea.deeplinktester.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.data.preferences.ThemeOption
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onExport: () -> Unit,
    onImport: () -> Unit,
    themeOption: ThemeOption,
    onThemeOptionSelected: (ThemeOption) -> Unit
) {
    var isThemeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = isThemeDropdownExpanded,
            onExpandedChange = { isThemeDropdownExpanded = !isThemeDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = themeOption.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) },
                onValueChange = {},
                readOnly = true,
                label = { Text("Theme") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isThemeDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isThemeDropdownExpanded,
                onDismissRequest = { isThemeDropdownExpanded = false }
            ) {
                ThemeOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }) },
                        onClick = {
                            onThemeOptionSelected(option)
                            isThemeDropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Divider()
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onExport) {
            Text("Export History")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onImport) {
            Text("Import History")
        }
    }
}
