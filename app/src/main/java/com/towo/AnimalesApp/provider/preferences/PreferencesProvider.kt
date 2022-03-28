package com.towo.AnimalesApp.provider.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by MoureDev by Brais Moure on 5/2/21.
 * www.mouredev.com
 */
enum class PreferencesKey(val value: String) {

    DIALOG("dialogo"),
    SOUND("sonido"),
    EFFECT("efecto"),
    POLITICS("politica"),
    ADS("no-ads"),
    FIRST_SYNC("firstSync")

}

object PreferencesProvider {

    fun set(context: Context, key: PreferencesKey, value: String) {
        val editor = prefs(context).edit()
        editor.putString(key.value, value).apply()
    }

    fun string(context: Context, key: PreferencesKey): String? {
        return prefs(context).getString(key.value, null)
    }

    fun set(context: Context, key: PreferencesKey, value: Boolean) {
        val editor = prefs(context).edit()
        editor.putBoolean(key.value, value).apply()
    }

    fun bool(context: Context, key: PreferencesKey): Boolean? {
        return prefs(context).getBoolean(key.value, true)
    }

    fun set(context: Context, key: PreferencesKey, value: Int) {
        val editor = prefs(context).edit()
        editor.putInt(key.value, value).apply()
    }

    fun int(context: Context, key: PreferencesKey): Int? {
        return prefs(context).getInt(key.value, 0)
    }

    fun remove(context: Context, key: PreferencesKey) {
        val editor = prefs(context).edit()
        editor.remove(key.value).apply()
    }

    // Elimina las SharedPreferences del dominio app
    fun clear(context: Context) {
        val editor = prefs(context).edit()
        editor.clear().apply()
    }

    // Private

    private fun prefs(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}