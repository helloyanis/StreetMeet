package com.helloyanis.streetmeet.utils

import android.app.ActivityManager
import android.content.Context

object ServiceUtils {
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses
        for (processInfo in runningAppProcesses) {
            if (processInfo.processName == serviceClass.name) {
                return true
            }
        }
        return false
    }
}