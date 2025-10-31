package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.*;
import java.util.*;

import com.example.elderlycareappnoai.databinding.ActivityEmergencyBinding;

public class EmergencyActivity extends AppCompatActivity {

    private ActivityEmergencyBinding binding;
    private ArrayList<String> contactList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        contactList = new ArrayList<>(prefs.getStringSet("contacts", new HashSet<>()));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        binding.contactListView.setAdapter(adapter);

        // Add contact button
        binding.addContactBtn.setOnClickListener(v -> {
            String name = binding.contactNameInput.getText().toString().trim();
            String number = binding.contactNumberInput.getText().toString().trim();

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
                return;
            }

            String contact = name + " - " + number;
            contactList.add(contact);
            adapter.notifyDataSetChanged();

            saveContacts();
            binding.contactNameInput.setText("");
            binding.contactNumberInput.setText("");
        });

        // Tap a contact to simulate calling
        binding.contactListView.setOnItemClickListener((parent, view, position, id) -> {
            String contact = contactList.get(position);
            Toast.makeText(this, "📞 Calling " + contact, Toast.LENGTH_SHORT).show();
        });

        // 🚨 Default emergency buttons
        binding.ambulanceBtn.setOnClickListener(v ->
                showMessage("🚑 Calling Ambulance (108)..."));

        binding.policeBtn.setOnClickListener(v ->
                showMessage("👮 Calling Police (100)..."));

        binding.fireBtn.setOnClickListener(v ->
                showMessage("🔥 Calling Fire Department (101)..."));

        binding.familyBtn.setOnClickListener(v ->
                showMessage("📞 Calling Family (+91 9876543210)..."));

        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EmergencyActivity.this, Maincode.class);
            startActivity(intent);
            finish();
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveContacts() {
        prefs.edit().putStringSet("contacts", new HashSet<>(contactList)).apply();
    }
}
