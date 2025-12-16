package com.example.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) return

        val result = ActivityRecognitionResult.extractResult(intent) ?: return

        val topActivity = result.mostProbableActivity

        var activityName = getTypeAsString(topActivity.type)

        if (topActivity.type == DetectedActivity.ON_FOOT) {
            val walking = result.probableActivities.find { it.type == DetectedActivity.WALKING }
            val running = result.probableActivities.find { it.type == DetectedActivity.RUNNING }

            if (walking != null && walking.confidence > 40) {
                activityName = "Na nogah (Hoja)"
            } else if (running != null && running.confidence > 40) {
                activityName = "Na nogah (Tek)"
            }
        }

        val broadcast = Intent("ACTIVITY_UPDATE")
        broadcast.putExtra("activity", activityName)
        broadcast.putExtra("confidence", topActivity.confidence)
        broadcast.setPackage(context.packageName)

        context.sendBroadcast(broadcast)
    }

    private fun getTypeAsString(type: Int): String {
        return when (type) {
            DetectedActivity.WALKING -> "Hoja"
            DetectedActivity.RUNNING -> "Tek"
            DetectedActivity.ON_FOOT -> "Na nogah"
            DetectedActivity.IN_VEHICLE -> "Vozilo"
            DetectedActivity.ON_BICYCLE -> "Kolo"
            DetectedActivity.STILL -> "Miruje"
            DetectedActivity.TILTING -> "Nagibanje"
            DetectedActivity.UNKNOWN -> "Neznano"
            else -> "Neznano"
        }
    }
}