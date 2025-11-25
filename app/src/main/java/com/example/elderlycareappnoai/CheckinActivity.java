package com.example.elderlycareappnoai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityCheckinBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckinActivity extends AppCompatActivity {

    private ActivityCheckinBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<String> checkinEntries;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginPage.class));
            finish();
            return;
        }

        // Show today’s date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        binding.dateText.setText("📅 Daily Check-In for " + today.format(formatter));

        // Setup Spinner
        String[] feelings = {"Good 😊", "Okay 😐", "Not Well 😔"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, feelings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusSpinner.setAdapter(spinnerAdapter);

        // Setup ListView
        checkinEntries = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, checkinEntries);
        binding.checkinList.setAdapter(adapter);

        loadCheckinHistory();

        // Handle Check-In Button
        binding.checkInButton.setOnClickListener(v -> addCheckin(today.format(formatter)));

        // Back to Main Menu
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void addCheckin(String todayStr) {
        db.collection("users").document(currentUser.getUid()).collection("checkins")
                .whereEqualTo("date", todayStr)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        binding.statusMessage.setText("✅ You already checked in today.");
                    } else {
                        String status = binding.statusSpinner.getSelectedItem().toString();

                        Map<String, Object> checkin = new HashMap<>();
                        checkin.put("date", todayStr);
                        checkin.put("status", status);

                        db.collection("users").document(currentUser.getUid()).collection("checkins")
                                .add(checkin)
                                .addOnSuccessListener(docRef -> {
                                    binding.statusMessage.setText("📝 Logged your check-in as: " + status);
                                    loadCheckinHistory();
                                })
                                .addOnFailureListener(e -> binding.statusMessage.setText("Error saving check-in."));
                    }
                });
    }

    private void loadCheckinHistory() {
        db.collection("users").document(currentUser.getUid()).collection("checkins")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkinEntries.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            checkinEntries.add(document.getString("date") + " - " + document.getString("status"));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
