package com.mensinator.app.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import android.os.Build
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DebugWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        provideContent {
            DebugWidgetContent(timestamp)
        }
    }
}

@Composable
private fun DebugWidgetContent(lastUpdated: String) {
    val cornerRadiusModifier = if (Build.VERSION.SDK_INT >= 31) {
        GlanceModifier.cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        GlanceModifier
    }

    MensinatorGlanceTheme {
        Box(
            modifier = cornerRadiusModifier
                .appWidgetBackground()
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DEBUG",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = "Last update",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = lastUpdated,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
