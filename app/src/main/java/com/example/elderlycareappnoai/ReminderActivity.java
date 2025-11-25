package com.example.elderlycareappnoai;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityReminderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {

    private ActivityReminderBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<String> reminders;
    private ArrayAdapter<String> adapter;

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

        reminders = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminders);
        binding.reminderList.setAdapter(adapter);

        loadReminders();

        binding.addReminderBtn.setOnClickListener(v -> addReminder());
        binding.deleteBtn.setOnClickListener(v -> deleteAllReminders());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.sendNotificationBtn.setOnClickListener(v -> showTimePickerDialog());
    }

    private void addReminder() {
        String reminderText = binding.reminderInput.getText().toString().trim();
        if (reminderText.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("text", reminderText);

        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .add(reminder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reminder added!", Toast.LENGTH_SHORT).show();
                    binding.reminderInput.setText("");
                    loadReminders();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error adding reminder: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadReminders() {
        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reminders.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            reminders.add(document.getString("text"));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading reminders.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAllReminders() {
        db.collection("users").document(currentUser.getUid()).collection("reminders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(this, "All reminders deleted.", Toast.LENGTH_SHORT).show();
                        loadReminders();
                    }
                });
    }

    private void showTimePickerDialog() {
        String reminderText = binding.reminderInput.getText().toString().trim();
        if (reminderText.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder text before setting a time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    long delay = NotificationHelper.getDelayForTime(hourOfDay, minuteOfHour);
                    NotificationHelper.scheduleNotification(this, delay, reminderText);
                    Toast.makeText(this,
                            "Reminder set for " + hourOfDay + ":" + String.format("%02d", minuteOfHour),
                            Toast.LENGTH_SHORT).show();
                }, hour, minute, false);
        dialog.show();
    }
}
