package com.example.elderlycareappnoai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.elderlycareappnoai.databinding.ActivityEmergencyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private static final int CALL_PHONE_PERMISSION_CODE = 102;

    private ActivityEmergencyBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<String> contactList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyBinding.inflate(getLayoutInflater());
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
        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        binding.contactListView.setAdapter(adapter);

        loadContacts();

        // Add contact button
        binding.addContactBtn.setOnClickListener(v -> addContact());
        binding.deleteContactsBtn.setOnClickListener(v -> deleteAllContacts());

        // Tap a contact to call
        binding.contactListView.setOnItemClickListener((parent, view, position, id) -> {
            String contactInfo = contactList.get(position);
            String[] parts = contactInfo.split(" - ");
            if (parts.length > 1) {
                makePhoneCall(parts[1]);
            }
        });

        // Default emergency buttons
        binding.ambulanceBtn.setOnClickListener(v -> makePhoneCall("108"));
        binding.policeBtn.setOnClickListener(v -> makePhoneCall("100"));
        binding.fireBtn.setOnClickListener(v -> makePhoneCall("101"));

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void addContact() {
        String name = binding.contactNameInput.getText().toString().trim();
        String number = binding.contactNumberInput.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Indian Phone Number Validation ---
        if (number.length() != 10) {
            binding.contactNumberInput.setError("Please enter a valid 10-digit mobile number.");
            return;
        }

        Map<String, Object> contact = new HashMap<>();
        contact.put("name", name);
        contact.put("number", number);

        db.collection("users").document(currentUser.getUid()).collection("emergency_contacts")
                .add(contact)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Contact added!", Toast.LENGTH_SHORT).show();
                    binding.contactNameInput.setText("");
                    binding.contactNumberInput.setText("");
                    loadContacts();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadContacts() {
        db.collection("users").document(currentUser.getUid()).collection("emergency_contacts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        contactList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            contactList.add(document.getString("name") + " - " + document.getString("number"));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAllContacts() {
        db.collection("users").document(currentUser.getUid()).collection("emergency_contacts")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(this, "All contacts deleted.", Toast.LENGTH_SHORT).show();
                        loadContacts();
                    }
                });
    }

    private void makePhoneCall(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_CODE);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. You can now make calls.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Cannot make calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
