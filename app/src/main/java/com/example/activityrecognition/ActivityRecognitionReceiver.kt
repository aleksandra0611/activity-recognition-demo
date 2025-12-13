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
        val activity = result.mostProbableActivity

        val activityName = when (activity.type) {
            DetectedActivity.WALKING -> "Hoja"
            DetectedActivity.RUNNING -> "Tek"
            DetectedActivity.IN_VEHICLE -> "Vozilo"
            DetectedActivity.ON_BICYCLE -> "Kolo"
            DetectedActivity.STILL -> "Miruje"
            else -> "Neznano"
        }

        val broadcast = Intent("ACTIVITY_UPDATE")
        broadcast.putExtra("activity", activityName)
        broadcast.putExtra("confidence", activity.confidence)

        broadcast.setPackage(context.packageName)

        context.sendBroadcast(broadcast)
    }
}