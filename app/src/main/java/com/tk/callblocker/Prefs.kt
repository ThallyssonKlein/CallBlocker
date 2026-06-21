package com.tk.callblocker

import android.content.Context

object Prefs {
    private const val FILE_NAME = "call_blocker_prefs"
    private const val KEY_ENABLED = "blocking_enabled"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun isBlockingEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setBlockingEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
}
