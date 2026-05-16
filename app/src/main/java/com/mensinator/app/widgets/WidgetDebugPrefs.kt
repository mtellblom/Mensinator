package com.mensinator.app.widgets

import android.content.Context

object WidgetDebugPrefs {
    private const val PREFS_NAME = "widget_debug_prefs"
    private const val KEY_UPDATE_INTERVAL_MINUTES = "update_interval_minutes"
    const val DEFAULT_INTERVAL_MINUTES = 1

    val intervalOptions = listOf(1, 2, 5, 10, 15, 30, 60)

    fun getIntervalMinutes(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_UPDATE_INTERVAL_MINUTES, DEFAULT_INTERVAL_MINUTES)

    fun setIntervalMinutes(context: Context, minutes: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_UPDATE_INTERVAL_MINUTES, minutes).apply()
    }

    fun getIntervalMillis(context: Context): Long = getIntervalMinutes(context) * 60_000L
}
