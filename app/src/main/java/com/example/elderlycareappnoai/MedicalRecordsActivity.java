package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.elderlycareappnoai.databinding.ActivityMedicalRecordsBinding;

import java.util.ArrayList;

public class MedicalRecordsActivity extends AppCompatActivity {

    private ActivityMedicalRecordsBinding binding;
    private ArrayList<String> medicalRecords;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize data
        medicalRecords = new ArrayList<>();
        medicalRecords.add("Blood Test - 12 Oct 2025");
        medicalRecords.add("X-Ray - 20 Sep 2025");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicalRecords);
        binding.recordsListView.setAdapter(adapter);

        // Add Record Button
        binding.addRecordBtn.setOnClickListener(v -> {
            String newRecord = binding.newRecordInput.getText().toString().trim();
            if (!newRecord.isEmpty()) {
                medicalRecords.add(newRecord);
                adapter.notifyDataSetChanged();
                binding.newRecordInput.setText("");
                Toast.makeText(this, "Record added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid record", Toast.LENGTH_SHORT).show();
            }
        });

        // Back Button
        binding.backBtn.setOnClickListener(v -> finish());
    }
}
