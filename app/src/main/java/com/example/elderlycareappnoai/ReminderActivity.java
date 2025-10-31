package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.elderlycareappnoai.databinding.ActivityReminderBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ReminderActivity extends AppCompatActivity {

    private ActivityReminderBinding binding;
    private ArrayList<String> reminders;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reminders = new ArrayList<>();

        // Load saved reminders from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppData", MODE_PRIVATE);
        Set<String> savedSet = prefs.getStringSet("reminders", new HashSet<>());
        reminders.addAll(savedSet);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminders);
        binding.reminderList.setAdapter(adapter);

        // Add reminder
        binding.addReminderBtn.setOnClickListener(v -> {
            String reminderText = binding.reminderInput.getText().toString().trim();
            if (reminderText.isEmpty()) {
                Toast.makeText(this, "Enter a reminder!", Toast.LENGTH_SHORT).show();
                return;
            }

            reminders.add(reminderText);
            adapter.notifyDataSetChanged();
            binding.reminderInput.setText("");
            Toast.makeText(this, "Reminder added!", Toast.LENGTH_SHORT).show();

            // Save updated reminders
            saveReminders();
        });

        // View all reminders
        binding.viewBtn.setOnClickListener(v -> {
            if (reminders.isEmpty()) {
                Toast.makeText(this, "No reminders yet!", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder all = new StringBuilder();
                for (int i = 0; i < reminders.size(); i++) {
                    all.append(i + 1).append(". ").append(reminders.get(i)).append("\n");
                }
                Toast.makeText(this, all.toString(), Toast.LENGTH_LONG).show();
            }
        });

        // Delete all reminders
        binding.deleteBtn.setOnClickListener(v -> {
            reminders.clear();
            adapter.notifyDataSetChanged();
            saveReminders();
            Toast.makeText(this, "All reminders deleted!", Toast.LENGTH_SHORT).show();
        });

        // Go to notification activity
        binding.notifyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });

        // Back to main menu
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void saveReminders() {
        SharedPreferences prefs = getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("reminders", new HashSet<>(reminders));
        editor.apply();
    }
}
