// Correct and clean Maincode.java
package com.example.elderlycareappnoai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.elderlycareappnoai.databinding.ActivityMaincodeBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Maincode extends AppCompatActivity {

    private ActivityMaincodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaincodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listeners for all buttons
        binding.medicalRecordsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MedicalRecordsActivity.class)));

        binding.remindersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ReminderActivity.class)));

        binding.entertainmentBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EntertainmentActivity.class)));

        binding.emergencyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyActivity.class)));

        binding.checkinBtn.setOnClickListener(v ->
                startActivity(new Intent(this, CheckinActivity.class)));

        binding.appointmentsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentsActivity.class)));

        binding.voiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Maincode.this, VoiceAssistantActivity.class);
            startActivity(intent);
        });

        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // <--- ADD THIS LINE

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(Maincode.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

}
