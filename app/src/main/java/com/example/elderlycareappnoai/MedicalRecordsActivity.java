package com.example.elderlycareappnoai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityMedicalRecordsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedicalRecordsActivity extends AppCompatActivity {

    private ActivityMedicalRecordsBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<String> medicalRecords;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginPage.class));
            finish();
            return;
        }

        // Setup ListView
        medicalRecords = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicalRecords);
        binding.recordsListView.setAdapter(adapter);

        loadMedicalRecords();

        // Add Record Button
        binding.addRecordBtn.setOnClickListener(v -> addMedicalRecord());
        binding.deleteRecordsBtn.setOnClickListener(v -> deleteAllMedicalRecords());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void addMedicalRecord() {
        String newRecord = binding.newRecordInput.getText().toString().trim();
        if (newRecord.isEmpty()) {
            Toast.makeText(this, "Please enter a valid record", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> record = new HashMap<>();
        record.put("recordText", newRecord);

        db.collection("users").document(currentUser.getUid()).collection("medical_records")
                .add(record)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Record added successfully", Toast.LENGTH_SHORT).show();
                    binding.newRecordInput.setText("");
                    loadMedicalRecords();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadMedicalRecords() {
        db.collection("users").document(currentUser.getUid()).collection("medical_records")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        medicalRecords.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            medicalRecords.add(document.getString("recordText"));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAllMedicalRecords() {
        db.collection("users").document(currentUser.getUid()).collection("medical_records")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(this, "All records deleted.", Toast.LENGTH_SHORT).show();
                        loadMedicalRecords();
                    }
                });
    }
}
