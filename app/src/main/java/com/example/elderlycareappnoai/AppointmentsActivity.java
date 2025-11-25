package com.example.elderlycareappnoai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityAppointmentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentsActivity extends AppCompatActivity {

    private ActivityAppointmentsBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<String> appointmentsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsBinding.inflate(getLayoutInflater());
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
        appointmentsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentsList);
        binding.listView.setAdapter(adapter);

        loadAppointments();

        // Add Appointment
        binding.addBtn.setOnClickListener(v -> addAppointment());
        binding.deleteBtn.setOnClickListener(v -> deleteAllAppointments());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void addAppointment() {
        String date = binding.dateInput.getText().toString().trim();
        String time = binding.timeInput.getText().toString().trim();
        String doctor = binding.doctorInput.getText().toString().trim();
        String notes = binding.notesInput.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || doctor.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("date", date);
        appointment.put("time", time);
        appointment.put("doctor", doctor);
        appointment.put("notes", notes);

        db.collection("users").document(currentUser.getUid()).collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment added!", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                    loadAppointments();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadAppointments() {
        db.collection("users").document(currentUser.getUid()).collection("appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentText = "📅 " + document.getString("date") + "  ⏰ " + document.getString("time") + "\n👨‍⚕️ " + document.getString("doctor");
                            if (document.contains("notes") && !document.getString("notes").isEmpty()) {
                                appointmentText += "\n📝 " + document.getString("notes");
                            }
                            appointmentsList.add(appointmentText);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAllAppointments() {
        db.collection("users").document(currentUser.getUid()).collection("appointments")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(this, "All appointments deleted.", Toast.LENGTH_SHORT).show();
                        loadAppointments();
                    }
                });
    }

    private void clearInputFields() {
        binding.dateInput.setText("");
        binding.timeInput.setText("");
        binding.doctorInput.setText("");
        binding.notesInput.setText("");
    }
}
