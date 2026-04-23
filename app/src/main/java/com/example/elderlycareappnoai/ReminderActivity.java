package com.example.elderlycareappnoai;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityReminderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ReminderActivity extends AppCompatActivity {

    private ActivityReminderBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<ReminderItem> reminderItems;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        NotificationHelper.createNotificationChannel(this);

        reminderItems = new ArrayList<>();
        adapter = new ReminderAdapter(this, reminderItems);
        binding.reminderList.setAdapter(adapter);

        // --- DEFINITIVE FIX: Receive voice command text ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(VoiceAssistantActivity.EXTRA_REMINDER_TEXT)) {
            String voiceText = intent.getStringExtra(VoiceAssistantActivity.EXTRA_REMINDER_TEXT);
            binding.reminderInput.setText(voiceText); // This prints the voice text into the box
        }

        loadReminders();

        binding.setReminderBtn.setOnClickListener(v -> showDatePickerDialog());
        binding.deleteSelectedBtn.setOnClickListener(v -> deleteSelectedReminders());
        binding.deleteBtn.setOnClickListener(v -> deleteAllReminders());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadReminders() {
        Set<String> checkedIds = new HashSet<>();
        for (ReminderItem item : reminderItems) {
            if (item.isChecked()) {
                checkedIds.add(item.getDocumentId());
            }
        }

        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reminderItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            String text = document.getString("text");
                            String time = document.getString("displayDateTime");

                            ReminderItem newItem = new ReminderItem(docId, time + ": " + text);
                            if (checkedIds.contains(docId)) {
                                newItem.setChecked(true);
                            }
                            reminderItems.add(newItem);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading reminders.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSelectedReminders() {
        ArrayList<ReminderItem> itemsToRemove = new ArrayList<>();
        for (ReminderItem item : reminderItems) {
            if (item.isChecked()) {
                db.collection("users").document(currentUser.getUid())
                        .collection("reminders").document(item.getDocumentId()).delete();
                itemsToRemove.add(item);
            }
        }
        if (itemsToRemove.isEmpty()) {
            Toast.makeText(this, "No reminders selected.", Toast.LENGTH_SHORT).show();
        } else {
            loadReminders();
            Toast.makeText(this, "Selected reminders deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        String reminderText = binding.reminderInput.getText().toString().trim();
        if (reminderText.isEmpty()) {
            Toast.makeText(this, "Please enter reminder text before setting a date and time.", Toast.LENGTH_LONG).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                askForExactAlarmPermission();
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> showTimePickerDialog(reminderText, year, month, dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(String reminderText, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> saveAndScheduleReminder(reminderText, year, month, dayOfMonth, hourOfDay, minute),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void saveAndScheduleReminder(String reminderText, int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.set(year, month, dayOfMonth, hour, minute, 0);
        long triggerAtMillis = scheduledCalendar.getTimeInMillis();
        if (triggerAtMillis <= System.currentTimeMillis()) {
            triggerAtMillis += AlarmManager.INTERVAL_DAY;
            scheduledCalendar.setTimeInMillis(triggerAtMillis);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.US);
        String displayDateTime = sdf.format(scheduledCalendar.getTime());
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("text", reminderText);
        reminder.put("displayDateTime", displayDateTime);
        reminder.put("timestamp", triggerAtMillis);
        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .add(reminder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show();
                    loadReminders();
                    binding.reminderInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        NotificationHelper.scheduleNotification(this, triggerAtMillis, "Reminder: " + reminderText, ReminderActivity.class);
    }

    private void deleteAllReminders() {
        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(this, "All reminders deleted.", Toast.LENGTH_SHORT).show();
                        loadReminders();
                    }
                });
    }

    private void askForExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                Toast.makeText(this, "Please grant permission to schedule reminders.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}
