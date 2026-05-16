package com.mensinator.app.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mensinator.app.BuildConfig
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module


val WidgetModule = module {
    single { WidgetPeriodDaysWithLabelWithBackgroundReceiver() }
    single { WidgetPeriodDaysWithoutLabelWithBackgroundReceiver() }
    single { WidgetPeriodDaysWithLabelWithoutBackgroundReceiver() }
    single { WidgetPeriodDaysWithoutLabelWithoutBackgroundReceiver() }
    if (BuildConfig.DEBUG) {
        single { DebugWidgetReceiver() }
    }
}

val WidgetInstances
    get() = buildList {
        add(get().get<WidgetPeriodDaysWithLabelWithBackgroundReceiver>())
        add(get().get<WidgetPeriodDaysWithoutLabelWithBackgroundReceiver>())
        add(get().get<WidgetPeriodDaysWithLabelWithoutBackgroundReceiver>())
        add(get().get<WidgetPeriodDaysWithoutLabelWithoutBackgroundReceiver>())
        if (BuildConfig.DEBUG) {
            add(get().get<DebugWidgetReceiver>())
        }
    }

abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Schedule midnight worker - WorkManager's REPLACE policy ensures no duplicates
        MidnightWorker.scheduleNextMidnight(context)
    }
}

class WidgetPeriodDaysWithLabelWithBackgroundReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BaseWidget(
        widgetType = WidgetType.Period,
        showLabel = true,
        showBackground = true
    )
}

class WidgetPeriodDaysWithoutLabelWithBackgroundReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BaseWidget(
        widgetType = WidgetType.Period,
        showLabel = false,
        showBackground = true
    )
}

class WidgetPeriodDaysWithLabelWithoutBackgroundReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BaseWidget(
        widgetType = WidgetType.Period,
        showLabel = true,
        showBackground = false
    )
}

class WidgetPeriodDaysWithoutLabelWithoutBackgroundReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BaseWidget(
        widgetType = WidgetType.Period,
        showLabel = false,
        showBackground = false
    )
}

class DebugWidgetReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DebugWidget()
}
