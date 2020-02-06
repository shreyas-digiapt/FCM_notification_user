package com.shreyas.fcmtesting.ui.Reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TestReciver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("qwerty", "asdas: ${intent!!.action}")
    }


}