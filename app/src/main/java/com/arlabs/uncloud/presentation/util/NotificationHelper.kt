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
        const val CHANNEL_ID = "daily_motivation_channel_v2" // V2 to force High Importance update
        const val MILESTONE_CHANNEL_ID = "milestone_channel_v2"
        const val NOTIFICATION_ID = 1001
        const val RANK_NOTIFICATION_ID = 1002
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Daily Motivation Channel (High Importance for Floating Notification)
            val name = "Daily Motivation"
            val descriptionText = "Shows daily motivational quotes and streak progress"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                    NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                        enableVibration(true)
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
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

        notify(builder, NOTIFICATION_ID)
    }

    fun showRankNotification(rankTitle: String, rankDesc: String) {
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
        val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Share Action specifically for Rank
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "👑 New Identity Unlocked: $rankTitle! \n\n" +
                    "I have reached the status of $rankTitle. \n\n" +
                    "#Uncloud #QuitSmoking")
            type = "text/plain"
        }
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent.createChooser(shareIntent, "Share Rank"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder =
                NotificationCompat.Builder(context, MILESTONE_CHANNEL_ID) // Reuse high priority milestone channel
                        .setSmallIcon(R.drawable.icon_app_logo)
                        .setContentTitle("\uD83D\uDC51 Rank Up: $rankTitle")
                        .setContentText(rankDesc)
                        .setStyle(NotificationCompat.BigTextStyle().bigText("You have evolved into $rankTitle.\n$rankDesc"))
                        .setColor(0xFFFFD700.toInt()) // Gold Color
                        .setColorized(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_menu_share, "Share", sharePendingIntent)

        notify(builder, RANK_NOTIFICATION_ID)
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
