package com.helloyanis.streetmeet

import android.app.ActivityManager
import android.content.Context

object ServiceUtils {
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Int.MAX_VALUE)
        for (runningService in services) {
            if (serviceClass.name == runningService.service.className) {
                return true
            }
        }
        return false
    }
}
