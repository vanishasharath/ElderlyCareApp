package com.example.elderlycareappnoai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.elderlycareappnoai.databinding.ActivityLoginPageBinding;

public class LoginPage extends AppCompatActivity {

    private ActivityLoginPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = prefs.getString("username", null);

        // ✅ If already logged in, go directly to Maincode
        if (savedName != null) {
            startActivity(new Intent(this, Maincode.class));
            finish();
            return;
        }

        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Register new user (now stays on login screen)
        binding.registerBtn.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString().trim();
            String phone = binding.phoneInput.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                binding.nameInput.setError("Enter name");
                binding.phoneInput.setError("Enter phone");
                return;
            }

            // Save new user details
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", name);
            editor.putString("phone", phone);
            editor.apply();

            // ✅ Show message instead of going to main page
            binding.nameInput.setText("");
            binding.phoneInput.setText("");
            binding.nameInput.setHint("Enter your name to login");
            binding.phoneInput.setHint("Enter your phone to login");

            // Optional: Toast message
            android.widget.Toast.makeText(this, "Registration successful! Please log in.", android.widget.Toast.LENGTH_SHORT).show();
        });

        // 🔹 Login existing user
        binding.loginBtn.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString().trim();
            String phone = binding.phoneInput.getText().toString().trim();

            String savedUser = prefs.getString("username", null);
            String savedPhone = prefs.getString("phone", null);

            if (name.equals(savedUser) && phone.equals(savedPhone)) {
                startActivity(new Intent(this, Maincode.class));
                finish();
            } else {
                binding.nameInput.setError("Invalid name");
                binding.phoneInput.setError("Invalid phone");
            }
        });
    }
}
