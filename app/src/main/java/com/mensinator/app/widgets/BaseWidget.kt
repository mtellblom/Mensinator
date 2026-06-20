package com.mensinator.app.widgets

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.mensinator.app.R
import com.mensinator.app.business.CalculationsHelper
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class BaseWidget : GlanceAppWidget(), KoinComponent {

    companion object {
        val SHOW_LABEL = booleanPreferencesKey("show_label")
        val SHOW_BACKGROUND = booleanPreferencesKey("show_background")
    }

    private val calculationsHelper: CalculationsHelper by inject()

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val showLabel = prefs[SHOW_LABEL] ?: true
            val showBackground = prefs[SHOW_BACKGROUND] ?: true
            val state = getData.collectAsState(WidgetData("", "", "", ""))
            WidgetContent(showLabel, showBackground, state)
        }
    }

    val getData = calculationsHelper.nextPeriod().combine(
        MidnightTrigger.midnightTrigger
    ) { nextPeriod, _ ->
        WidgetData(
            daysUntilPeriodWithoutText = formatDaysUntilPeriod(nextPeriod, NextPeriodFormat.OnlyDays),
            daysUntilPeriodWithText = formatDaysUntilPeriod(nextPeriod, NextPeriodFormat.MediumLengthText),
            daysUntilOvulationWithText = "",
            daysUntilOvulationWithoutText = ""
        )
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        super.providePreview(context, widgetCategory)
        provideContent {
            val state = remember {
                mutableStateOf(
                    WidgetData(
                        daysUntilPeriodWithoutText = "10",
                        daysUntilPeriodWithText = "Period in 10 days",
                        daysUntilOvulationWithoutText = "",
                        daysUntilOvulationWithText = "",
                    )
                )
            }
            WidgetContent(showLabel = true, showBackground = true, state)
        }
    }

    @Composable
    private fun WidgetContent(showLabel: Boolean, showBackground: Boolean, state: State<WidgetData>) {
        val data = state.value
        val context = LocalContext.current
        val label = context.getString(R.string.widget_period_abbreviation)

        MensinatorGlanceTheme {
            if (showLabel) {
                WidgetContentWithLabel(text = data.daysUntilPeriodWithText, showBackground = showBackground)
            } else {
                WidgetContentWithoutLabel(text = data.daysUntilPeriodWithoutText, label = label, showBackground = showBackground)
            }
        }
    }

    sealed interface NextPeriodFormat {
        data object OnlyDays : NextPeriodFormat
        data object MediumLengthText : NextPeriodFormat
    }

    private fun formatDaysUntilPeriod(date: LocalDate?, format: NextPeriodFormat): String {
        val daysUntilNextPeriod = LocalDate.now().until(date).days
        return when (format) {
            NextPeriodFormat.OnlyDays -> if (date == null) "?" else "$daysUntilNextPeriod"
            NextPeriodFormat.MediumLengthText -> if (date == null) "Unknown" else "Period in $daysUntilNextPeriod days"
        }
    }
}
