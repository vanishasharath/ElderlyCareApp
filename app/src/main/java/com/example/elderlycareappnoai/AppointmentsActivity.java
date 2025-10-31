package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.elderlycareappnoai.databinding.ActivityAppointmentsBinding;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppointmentsActivity extends AppCompatActivity {

    private ActivityAppointmentsBinding binding;
    private ArrayList<String> appointmentsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appointmentsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentsList);
        binding.listView.setAdapter(adapter);

        // 🔁 Load saved appointments from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppData", MODE_PRIVATE);
        Set<String> savedAppointments = prefs.getStringSet("appointments", new HashSet<>());
        appointmentsList.addAll(savedAppointments);
        adapter.notifyDataSetChanged();

        // ➕ Add Appointment
        binding.addBtn.setOnClickListener(v -> {
            String date = binding.dateInput.getText().toString().trim();
            String time = binding.timeInput.getText().toString().trim();
            String doctor = binding.doctorInput.getText().toString().trim();
            String notes = binding.notesInput.getText().toString().trim();

            if (date.isEmpty() || time.isEmpty() || doctor.isEmpty()) {
                Toast.makeText(this, "Please enter all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            String appointmentText = "📅 " + date + "  ⏰ " + time + "\n👨‍⚕️ " + doctor;
            if (!notes.isEmpty()) appointmentText += "\n📝 " + notes;

            appointmentsList.add(appointmentText);
            adapter.notifyDataSetChanged();

            // 🧠 Save to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            Set<String> updatedAppointments = new HashSet<>(appointmentsList);
            editor.putStringSet("appointments", updatedAppointments);
            editor.apply();

            // Clear input fields
            binding.dateInput.setText("");
            binding.timeInput.setText("");
            binding.doctorInput.setText("");
            binding.notesInput.setText("");

            Toast.makeText(this, "Appointment added!", Toast.LENGTH_SHORT).show();
        });

        // 🗑️ Delete All Appointments
        binding.deleteBtn.setOnClickListener(v -> {
            appointmentsList.clear();
            adapter.notifyDataSetChanged();

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("appointments");
            editor.apply();

            Toast.makeText(this, "All appointments deleted!", Toast.LENGTH_SHORT).show();
        });

        // 🔙 Back to Main Menu
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AppointmentsActivity.this, Maincode.class);
            startActivity(intent);
            finish();
        });
    }
}
