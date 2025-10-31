package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.content.SharedPreferences;

public class Maincode extends AppCompatActivity {

    Button medicalRecordsBtn, remindersBtn, entertainmentBtn, emergencyBtn, checkinBtn, appointmentsBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincode);

        medicalRecordsBtn = findViewById(R.id.medicalRecordsBtn);
        remindersBtn = findViewById(R.id.remindersBtn);
        entertainmentBtn = findViewById(R.id.entertainmentBtn);
        emergencyBtn = findViewById(R.id.emergencyBtn);
        checkinBtn = findViewById(R.id.checkinBtn);
        appointmentsBtn = findViewById(R.id.appointmentsBtn);
        logoutBtn = findViewById(R.id.logoutBtn); // ✅ New logout button

        medicalRecordsBtn.setOnClickListener(v -> startActivity(new Intent(this, MedicalRecordsActivity.class)));
        remindersBtn.setOnClickListener(v -> startActivity(new Intent(this, ReminderActivity.class)));
        entertainmentBtn.setOnClickListener(v -> startActivity(new Intent(this, EntertainmentActivity.class)));
        emergencyBtn.setOnClickListener(v -> startActivity(new Intent(this, EmergencyActivity.class)));
        checkinBtn.setOnClickListener(v -> startActivity(new Intent(this, CheckinActivity.class)));
        appointmentsBtn.setOnClickListener(v -> startActivity(new Intent(this, AppointmentsActivity.class)));

        // ✅ Logout button action
        logoutBtn.setOnClickListener(v -> {
            // Clear saved user data
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Redirect to login
            Intent intent = new Intent(Maincode.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
