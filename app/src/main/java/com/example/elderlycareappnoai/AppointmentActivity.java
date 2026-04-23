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

import com.example.elderlycareappnoai.databinding.ActivityAppointmentsBinding;
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

public class AppointmentActivity extends AppCompatActivity {

    private ActivityAppointmentsBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<AppointmentItem> appointmentItems;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        appointmentItems = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentItems);
        binding.appointmentsListView.setAdapter(adapter);

        loadAppointments();

        binding.setAppointmentBtn.setOnClickListener(v -> showDatePickerDialog());
        binding.deleteSelectedBtn.setOnClickListener(v -> deleteSelectedAppointments());
        binding.deleteBtn.setOnClickListener(v -> deleteAllAppointments());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadAppointments() {
        Set<String> checkedIds = new HashSet<>();
        for (AppointmentItem item : appointmentItems) {
            if (item.isChecked()) {
                checkedIds.add(item.getDocumentId());
            }
        }

        db.collection("users").document(currentUser.getUid()).collection("appointments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            String text = document.getString("text");
                            String time = document.getString("displayDateTime");

                            AppointmentItem newItem = new AppointmentItem(docId, time + ": " + text);
                            if (checkedIds.contains(docId)) {
                                newItem.setChecked(true);
                            }
                            appointmentItems.add(newItem);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading appointments.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSelectedAppointments() {
        ArrayList<AppointmentItem> itemsToRemove = new ArrayList<>();
        for (AppointmentItem item : appointmentItems) {
            if (item.isChecked()) {
                db.collection("users").document(currentUser.getUid())
                        .collection("appointments").document(item.getDocumentId()).delete();
                itemsToRemove.add(item);
            }
        }
        if (itemsToRemove.isEmpty()) {
            Toast.makeText(this, "No appointments selected.", Toast.LENGTH_SHORT).show();
        } else {
            loadAppointments();
            Toast.makeText(this, "Selected appointments deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        String appointmentText = binding.appointmentInput.getText().toString().trim();
        if (appointmentText.isEmpty()) {
            Toast.makeText(this, "Please enter appointment details before setting a date.", Toast.LENGTH_LONG).show();
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
                (view, year, month, dayOfMonth) -> showTimePickerDialog(appointmentText, year, month, dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(String appointmentText, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> saveAndScheduleAppointment(appointmentText, year, month, dayOfMonth, hourOfDay, minute),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void saveAndScheduleAppointment(String appointmentText, int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.set(year, month, dayOfMonth, hour, minute, 0);
        long triggerAtMillis = scheduledCalendar.getTimeInMillis();
        if (triggerAtMillis <= System.currentTimeMillis()) {
            triggerAtMillis += AlarmManager.INTERVAL_DAY;
            scheduledCalendar.setTimeInMillis(triggerAtMillis);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.US);
        String displayDateTime = sdf.format(scheduledCalendar.getTime());
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("text", appointmentText);
        appointment.put("displayDateTime", displayDateTime);
        appointment.put("timestamp", triggerAtMillis);
        db.collection("users").document(currentUser.getUid()).collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment Set!", Toast.LENGTH_SHORT).show();
                    loadAppointments();
                    binding.appointmentInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        NotificationHelper.scheduleNotification(this, triggerAtMillis, "Appointment: " + appointmentText, AppointmentActivity.class);
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

    private void askForExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                Toast.makeText(this, "Please grant permission to schedule appointments.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}
