package com.example.activityrecognition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient

class MainActivity : AppCompatActivity() {

    private lateinit var client: ActivityRecognitionClient
    private lateinit var pendingIntent: PendingIntent

    private val activityUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val activity = intent.getStringExtra("activity")
            val confidence = intent.getIntExtra("confidence", 0)

            findViewById<TextView>(R.id.tvActivity).text =
                "Aktivnost: $activity"
            findViewById<TextView>(R.id.tvConfidence).text =
                "Zanesljivost: $confidence %"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = ActivityRecognition.getClient(this)

        val intent = Intent(this, ActivityRecognitionReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        registerReceiver(activityUpdateReceiver, IntentFilter("ACTIVITY_UPDATE"), Context.RECEIVER_NOT_EXPORTED)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            requestPermissionAndStart()
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                client.removeActivityUpdates(pendingIntent)
                    .addOnSuccessListener {
                        android.widget.Toast.makeText(this, "Stopped successfully", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        android.widget.Toast.makeText(this, "Failed to stop", android.widget.Toast.LENGTH_SHORT).show()
                    }
            } else {
                android.widget.Toast.makeText(this, "Permission missing, cannot stop manually", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissionAndStart() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                100
            )
            return
        }

        client.requestActivityUpdates(5000, pendingIntent)
            .addOnSuccessListener {
                android.widget.Toast.makeText(this, "Success! Updates started. Move your phone!", android.widget.Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                android.widget.Toast.makeText(this, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
    }
}