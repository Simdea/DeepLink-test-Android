package com.simdea.deeplinktester.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.simdea.deeplinktester.R

@Composable
fun AppIcon() {
    // Get the current context
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(R.mipmap.ic_launcher_round) // Key change: Pass the Drawable directly.
            .allowHardware(false) // Still important for adaptive icons.
            .crossfade(true)
            .build(),
        error = painterResource(id = android.R.drawable.ic_dialog_alert)
    )


    Image(
        painter = painter,
        contentDescription = "App Icon",
        modifier = Modifier.size(55.dp)
    )
}

@Preview
@Composable
fun AppIconPreview() {
    AppIcon()
}