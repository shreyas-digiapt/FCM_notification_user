package com.shreyas.fcmtesting

import android.content.Context

class Utiles {

    companion object {

        val APPNAME = "MYAPP"
        val MODE = 0
        val NAME = "NAME"
        val TOKEN = "TOKEN"

        fun setPrefs(context: Context, name:String, token:String?) {
            val prefs = context.getSharedPreferences(APPNAME, MODE)
            prefs.edit().putString(NAME, name).putString(TOKEN, token).apply()
        }

        fun getPrefsName(context: Context): String? {
            val prefs = context.getSharedPreferences(APPNAME, MODE)
            return prefs.getString(NAME, null)
        }

        fun getPrefsToken(context: Context): String? {
            val prefs = context.getSharedPreferences(APPNAME, MODE)
            return prefs.getString(TOKEN, null)
        }
    }
}