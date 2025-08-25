package com.simdea.deeplinktester.ui.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
            adView.loadAd(AdRequest.Builder().build())
            adView
        }
    )
}
