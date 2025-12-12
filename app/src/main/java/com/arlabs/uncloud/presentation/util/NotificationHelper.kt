package com.arlabs.uncloud.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.arlabs.uncloud.R
import com.arlabs.uncloud.presentation.MainActivity
import androidx.core.graphics.createBitmap

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "daily_motivation_channel"
        const val MILESTONE_CHANNEL_ID = "milestone_channel"
        const val NOTIFICATION_ID = 1001
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Daily Motivation Channel (Default Importance)
            val name = "Daily Motivation"
            val descriptionText = "Shows daily motivational quotes and streak progress"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                    NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                    }
            notificationManager.createNotificationChannel(channel)

            // Milestone Channel (High Importance for Heads-up)
            val milestoneName = "Health Milestones"
            val milestoneDesc = "Alerts when you reach a new health milestone"
            val milestoneImportance = NotificationManager.IMPORTANCE_HIGH
            val milestoneChannel =
                    NotificationChannel(MILESTONE_CHANNEL_ID, milestoneName, milestoneImportance).apply {
                        description = milestoneDesc
                        enableVibration(true)
                    }
            notificationManager.createNotificationChannel(milestoneChannel)
        }
    }

    fun showNotification(title: String, content: String) {
        // Create an explicit intent for an Activity in your app
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
        val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_flame)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

        notify(builder, NOTIFICATION_ID)
    }

    fun showMilestoneNotification(title: String, content: String) {
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
        val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Share Action
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "🚀 Milestone Unlocked: $title! \n\n" +
                    "My body is healing: $content \n\n" +
                    "Track your own recovery with #Uncloud ☁️")
            type = "text/plain"
        }
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent.createChooser(shareIntent, "Share Milestone"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = vectorToBitmap(context, R.drawable.icon_trophy) // Placeholder for trophy

        val builder =
                NotificationCompat.Builder(context, MILESTONE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_app_logo)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("🎉 $title")
                        .setContentText(content)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                        .setColor(0xFF00E5FF.toInt()) // Cyan Accent
                        .setColorized(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) // For Pre-Oreo
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_menu_share, "Share", sharePendingIntent)

        notify(builder, (System.currentTimeMillis() % 10000).toInt())
    }

    private fun notify(builder: NotificationCompat.Builder, id: Int) {
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(id, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun vectorToBitmap(context: Context, drawableId: Int): android.graphics.Bitmap? {
        val drawable = androidx.core.content.ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
