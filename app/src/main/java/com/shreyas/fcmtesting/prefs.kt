package com.shreyas.fcmtesting

import android.content.Context
import android.content.SharedPreferences

val PREFNAME: String = "MYPREF"
val MODE: Int = 0
val COMMAND: String = "COMMAND"

fun getCount(context: Context):Int {
    val pref = getCommandPrefs(context)
    return pref.getInt(COMMAND, 0)
}

fun getCommand(context: Context): SharedPreferenceStringLiveData {
    return SharedPreferenceStringLiveData(getCommandPrefs(context), COMMAND, "null")
}

fun getCommandPrefs(context: Context): SharedPreferences {

    return context.getSharedPreferences(PREFNAME, MODE)
}

fun setCommand(context: Context, command: String) {
        val pref = getCommandPrefs(context)
        pref.edit().also {
            it.putString(COMMAND, command)
            it.apply()
        }
}