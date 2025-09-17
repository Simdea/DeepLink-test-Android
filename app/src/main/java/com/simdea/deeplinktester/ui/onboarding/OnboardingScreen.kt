package com.simdea.deeplinktester.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.ui.composables.PrimaryButton
import com.simdea.deeplinktester.R
import com.simdea.deeplinktester.ui.theme.DeepLinkTestAndroidTheme

@Composable
fun OnboardingScreen(onOnboardingCompleted: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Image(
                painterResource(R.mipmap.ic_launcher_round),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Deeplink Tester",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome! This app helps you test and manage your deeplinks with ease.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            FeatureItem(
                icon = Icons.Outlined.QrCodeScanner,
                title = "Scan & Test",
                description = "Test any URI or scan QR codes instantly."
            )
            FeatureItem(
                icon = Icons.Outlined.Edit,
                title = "Edit & Validate",
                description = "Easily edit URI parameters and validate syntax."
            )
            FeatureItem(
                icon = Icons.Outlined.History,
                title = "History & Favorites",
                description = "Keep track of your tests with searchable history and favorites."
            )
            FeatureItem(
                icon = Icons.Outlined.Folder,
                title = "Organize & Share",
                description = "Create collections, and export/import your data."
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Get Started",
                onClick = onOnboardingCompleted
            )
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(onOnboardingCompleted = {})
}

@Preview
@Composable
fun FeatureItemPreview() {
    FeatureItem(
        icon = Icons.Outlined.QrCodeScanner,
        title = "Scan & Test",
        description = "Test any URI or scan QR codes instantly.")
}

@Composable
private fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
