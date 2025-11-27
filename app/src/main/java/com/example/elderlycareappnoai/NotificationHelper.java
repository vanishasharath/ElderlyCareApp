package com.example.elderlycareappnoai; // Or your project's package name

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends BroadcastReceiver {

    public static final String CHANNEL_ID = "ReminderChannel";
    public static final String NOTIFICATION_TITLE = "Elderly Care Reminder";
    public static final String NOTIFICATION_MESSAGE_KEY = "notification_message";
    // --- DEFINITIVE FIX: Key to specify which activity to open ---
    public static final String ACTIVITY_CLASS_KEY = "activity_class";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(NOTIFICATION_MESSAGE_KEY);
        if (message == null) {
            message = "You have a new reminder!";
        }

        // --- DEFINITIVE FIX: Dynamically determine which activity to open ---
        Class<?> targetActivityClass = (Class<?>) intent.getSerializableExtra(ACTIVITY_CLASS_KEY);
        // Default to ReminderActivity if, for some reason, the extra is not provided
        if (targetActivityClass == null) {
            targetActivityClass = ReminderActivity.class;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create an Intent that will open the correct activity
        Intent activityIntent = new Intent(context, targetActivityClass);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), // Use a new request code to ensure it's unique
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Notification disappears after being tapped
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Vibrate and make a sound
                .setContentIntent(contentIntent); // Attach the tap action

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for elderly care reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // --- DEFINITIVE FIX: Overloaded method to schedule a notification for a specific activity ---
    public static void scheduleNotification(Context context, long triggerAtMillis, String message, Class<?> targetActivityClass) {
        Intent intent = new Intent(context, NotificationHelper.class);
        intent.putExtra(NOTIFICATION_MESSAGE_KEY, message);
        intent.putExtra(ACTIVITY_CLASS_KEY, targetActivityClass); // Pass the target class

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) triggerAtMillis, // Use the trigger time as a unique request code
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // The calling activity is responsible for handling this permission state.
                return;
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}