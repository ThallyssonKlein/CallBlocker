package com.tk.callblocker

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object Prefs {
    private const val FILE_NAME = "call_blocker_prefs"
    private const val KEY_ENABLED = "blocking_enabled"
    private const val KEY_BLOCKED_LOGS = "blocked_logs"
    private const val MAX_LOGS = 200

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun isBlockingEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setBlockingEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun addBlockedCallLog(context: Context, number: String) {
        val prefs = prefs(context)
        val existing = JSONArray(prefs.getString(KEY_BLOCKED_LOGS, "[]"))
        val entry = JSONObject().apply {
            put("number", number)
            put("timestamp", System.currentTimeMillis())
        }
        val updated = JSONArray()
        updated.put(entry)
        for (i in 0 until minOf(existing.length(), MAX_LOGS - 1)) {
            updated.put(existing.getJSONObject(i))
        }
        prefs.edit().putString(KEY_BLOCKED_LOGS, updated.toString()).apply()
    }

    fun getBlockedCallLogs(context: Context): List<BlockedCallEntry> {
        val json = JSONArray(prefs(context).getString(KEY_BLOCKED_LOGS, "[]"))
        return (0 until json.length()).map {
            val obj = json.getJSONObject(it)
            BlockedCallEntry(obj.getString("number"), obj.getLong("timestamp"))
        }
    }

    fun clearBlockedCallLogs(context: Context) {
        prefs(context).edit().remove(KEY_BLOCKED_LOGS).apply()
    }
}
