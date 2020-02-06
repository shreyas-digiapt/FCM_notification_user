package com.shreyas.fcmtesting.ui.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class Woker(private val context: Context, private val params:WorkerParameters) : Worker(context, params) {

    override fun doWork(): ListenableWorker.Result {

        return ListenableWorker.Result.success()
    }


}