package com.mensinator.app.widgets

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import com.mensinator.app.R
import com.mensinator.app.ui.theme.MensinatorTheme
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            MensinatorTheme {
                WidgetConfigScreen(onDesignSelected = ::applyConfig)
            }
        }
    }

    private fun applyConfig(showLabel: Boolean, showBackground: Boolean) {
        val context = this
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { current ->
                current.toMutablePreferences().apply {
                    this[BaseWidget.SHOW_LABEL] = showLabel
                    this[BaseWidget.SHOW_BACKGROUND] = showBackground
                }
            }
            BaseWidget().update(context, glanceId)
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}

@Composable
private fun WidgetConfigScreen(onDesignSelected: (showLabel: Boolean, showBackground: Boolean) -> Unit) {
    val designs = listOf(
        Triple(true, true, R.string.widget_config_label_with_bg),
        Triple(true, false, R.string.widget_config_label_no_bg),
        Triple(false, true, R.string.widget_config_no_label_with_bg),
        Triple(false, false, R.string.widget_config_no_label_no_bg),
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.widget_config_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            designs.forEach { (showLabel, showBackground, labelRes) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDesignSelected(showLabel, showBackground) }
                ) {
                    Text(
                        text = stringResource(labelRes),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
