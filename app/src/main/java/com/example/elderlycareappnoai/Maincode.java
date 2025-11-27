package com.example.elderlycareappnoai;

import android.content.Intent;
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

        // --- DEFINITIVE FIX: Point to the correct AppointmentActivity class ---
        binding.appointmentsBtn.setOnClickListener(v -> startActivity(new Intent(this, AppointmentActivity.class)));
        
        binding.remindersBtn.setOnClickListener(v -> startActivity(new Intent(this, ReminderActivity.class)));
        binding.medicalRecordsBtn.setOnClickListener(v -> startActivity(new Intent(this, MedicalRecordsActivity.class)));
        binding.emergencyBtn.setOnClickListener(v -> startActivity(new Intent(this, EmergencyActivity.class)));
        binding.checkinBtn.setOnClickListener(v -> startActivity(new Intent(this, CheckinActivity.class)));
        binding.voiceBtn.setOnClickListener(v -> startActivity(new Intent(this, VoiceAssistantActivity.class)));
        binding.entertainmentBtn.setOnClickListener(v -> startActivity(new Intent(this, EntertainmentActivity.class)));
        binding.settingsBtn.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Maincode.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}