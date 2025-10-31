package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.content.Intent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.elderlycareappnoai.databinding.ActivityCheckinBinding;

public class CheckinActivity extends AppCompatActivity {

    private ActivityCheckinBinding binding;
    private ArrayList<String> checkinEntries = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static HashMap<LocalDate, String> checkInLog = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show today’s date
        LocalDate today = LocalDate.now();
        binding.dateText.setText("📅 Daily Check-In for " + today);

        // Setup Spinner
        String[] feelings = {"Good 😊", "Okay 😐", "Not Well 😔"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, feelings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusSpinner.setAdapter(spinnerAdapter);

        // Setup ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, checkinEntries);
        binding.checkinList.setAdapter(adapter);

        // Handle Check-In Button
        binding.checkInButton.setOnClickListener(v -> {
            if (checkInLog.containsKey(today)) {
                binding.statusMessage.setText("✅ You already checked in today: " + checkInLog.get(today));
                return;
            }

            String status = binding.statusSpinner.getSelectedItem().toString();
            checkInLog.put(today, status);
            checkinEntries.add(today + " - " + status);
            adapter.notifyDataSetChanged();

            binding.statusMessage.setText("📝 Logged your check-in as: " + status);
        });

        // 🔙 Back to Main Menu
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CheckinActivity.this, Maincode.class);
            startActivity(intent);
            finish();
        });
    }
}
