package com.mensinator.app.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        MidnightWorker.scheduleNextMidnight(context)
    }
}

class MensinatorWidgetReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BaseWidget()
}
