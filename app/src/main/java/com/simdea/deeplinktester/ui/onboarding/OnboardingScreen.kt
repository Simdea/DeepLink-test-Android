package com.simdea.deeplinktester.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.simdea.deeplinktester.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(onOnboardingCompleted: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Test Any Deeplink",
            description = "Enter any URI and launch it directly from the app.",
            imageRes = R.drawable.ic_launcher_foreground // Placeholder
        ),
        OnboardingPage(
            title = "QR Code Scanner",
            description = "Scan QR codes to instantly populate the deeplink input field.",
            imageRes = R.drawable.ic_launcher_foreground // Placeholder
        ),
        OnboardingPage(
            title = "History & Favorites",
            description = "Automatically saves a history of all tested deeplinks, with the ability to mark your most-used links as favorites.",
            imageRes = R.drawable.ic_launcher_foreground // Placeholder
        ),
        OnboardingPage(
            title = "Collections",
            description = "Group your deeplinks into named collections to keep your workspace organized.",
            imageRes = R.drawable.ic_launcher_foreground // Placeholder
        ),
        OnboardingPage(
            title = "Export/Import",
            description = "Export your history to a JSON file to share with colleagues or back it up, and import it back into the app.",
            imageRes = R.drawable.ic_launcher_foreground // Placeholder
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage != pages.size - 1) {
                TextButton(onClick = onOnboardingCompleted) {
                    Text("Skip")
                }
            } else {
                Spacer(Modifier.width(64.dp)) // To balance the row
            }

            Row(
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(12.dp)
                    )
                }
            }

            if (pagerState.currentPage != pages.size - 1) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text("Next")
                }
            } else {
                Button(onClick = onOnboardingCompleted) {
                    Text("Get Started")
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
