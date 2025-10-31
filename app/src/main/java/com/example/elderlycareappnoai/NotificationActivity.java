package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.example.elderlycareappnoai.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show notifications (dummy for now)
        binding.showNotificationsBtn.setOnClickListener(v -> {
            Toast.makeText(this, "No new notifications right now.", Toast.LENGTH_SHORT).show();
        });

        // Back button
        binding.backBtn.setOnClickListener(v -> finish());
    }
}
