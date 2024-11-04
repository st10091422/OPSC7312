package com.opsc.opsc7312poe.api.local

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.opsc.opsc7312poe.R

class NotificationManager (private val context: Context){
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 0

    private fun createNotificationChannel(){
        // This method was taken from YouTube
        // https://youtu.be/urn355_ymNA?si=ST8_Uds_m2zauUd7
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                lightColor = Color.GREEN
                enableLights(true)
                description = "Channel for general notifications"
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        // Check for notification permission (required for Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission from the user
                ActivityCompat.requestPermissions(
                    (context as Activity),  // Cast context to Activity to request permission
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_ID
                )
                return
            }
        }

        // Create notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_default_orange_900)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)  // Dismiss the notification after it is clicked
            .build()

        // Show notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun registerUser(){
        createNotificationChannel()
        showNotification(context.getString(R.string.new_user), context.getString(R.string.user_registered))
    }

    fun createCategoryNotification(){
        createNotificationChannel()
        showNotification(context.getString(R.string.new_category), context.getString(R.string.new_category_created))
    }

    fun createExpenseNotification(){
        createNotificationChannel()
        showNotification(context.getString(R.string.new_expense), context.getString(R.string.new_expense_created))
    }

    fun createIncomeNotification(){
        createNotificationChannel()
        showNotification(context.getString(R.string.new_income), context.getString(R.string.new_income_created))
    }

    fun syncSuccessfulNotification(){
        createNotificationChannel()
        showNotification(context.getString(R.string.sync), context.getString(R.string.successful_sync))
    }

    fun syncFailNotification(){
        createNotificationChannel()
        showNotification(context.getString(R.string.sync), context.getString(R.string.fsiled_sync))
    }
}