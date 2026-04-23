package com.example.elderlycareappnoai;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.elderlycareappnoai.databinding.ActivityChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private GenerativeModelFutures generativeModel;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the Gemini SDK
        try {
            GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.GEMINI_API_KEY);
            generativeModel = GenerativeModelFutures.from(gm);
        } catch (Exception e) {
            binding.chatView.setText("AI Error: Could not initialize model.");
            return;
        }

        binding.chatView.setMovementMethod(new ScrollingMovementMethod());

        binding.sendButton.setOnClickListener(v -> {
            String userInput = binding.userInput.getText().toString().trim();
            if (userInput.isEmpty()) {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Logic starts here
            processChatCommand(userInput);
        });
    }

    // --- DEFINITIVE FIX: Logic to recognize reminder commands ---
    private void processChatCommand(String input) {
        binding.chatView.append("You: " + input + "\n\n");
        binding.userInput.setText("");
        binding.sendButton.setEnabled(false);

        String lowerCaseInput = input.toLowerCase();

        if (lowerCaseInput.contains("remind me to")) {
            // Extract the reminder text
            int index = lowerCaseInput.indexOf("remind me to");
            String reminderText = input.substring(index + "remind me to".length()).trim();

            if (reminderText.isEmpty()) {
                displayAIResponse("What would you like to be reminded of?");
            } else {
                displayAIResponse("Okay! Opening reminders to set an alarm for: " + reminderText);

                // Launch ReminderActivity and pass the text
                Intent intent = new Intent(this, ReminderActivity.class);
                intent.putExtra(VoiceAssistantActivity.EXTRA_REMINDER_TEXT, reminderText);
                startActivity(intent);
            }
        } else {
            // If it's not a command, just chat with Gemini
            sendTextToGemini(input);
        }
    }

    private void sendTextToGemini(String userText) {
        Content content = new Content.Builder().addText(userText).build();
        Executor mainExecutor = ContextCompat.getMainExecutor(this);

        backgroundExecutor.execute(() -> {
            try {
                GenerateContentResponse response = generativeModel.generateContent(content).get();
                String aiReply = response.getText();
                mainExecutor.execute(() -> displayAIResponse(aiReply != null ? aiReply.trim() : "No response received."));
            } catch (Exception e) {
                mainExecutor.execute(() -> displayAIResponse("AI Error: " + e.getMessage()));
            }
        });
    }

    private void displayAIResponse(String text) {
        binding.chatView.append("AI: " + text + "\n\n");
        binding.sendButton.setEnabled(true);
    }
}