package com.example.elderlycareappnoai;

import android.Manifest;
import android.content.ActivityNotFoundException;import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.elderlycareappnoai.databinding.ActivityVoiceAssistantBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VoiceAssistantActivity extends AppCompatActivity {

    public static final String EXTRA_REMINDER_TEXT = "com.example.elderlycareappnoai.REMINDER_TEXT";
    private ActivityVoiceAssistantBinding binding;
    private TextToSpeech tts;
    private GenerativeModelFutures generativeModel;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        String recognizedText = matches.get(0);
                        binding.textView.setText("You said:\n" + recognizedText);
                        processVoiceCommand(recognizedText);
                    }
                } else {
                    binding.textView.setText("Could not hear you. Please try again.");
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Microphone permission is required.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Gemini SDK
        try {
            GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.GEMINI_API_KEY);
            generativeModel = GenerativeModelFutures.from(gm);
        } catch (Exception e) {
            binding.textView.setText("AI Error: Could not initialize model.");
            return;
        }

        // --- DEFINITIVE FIX: Initialize Text-To-Speech ---
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        binding.startButton.setOnClickListener(v -> startListening());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            try {
                speechRecognitionLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Voice input not supported on this device.", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void processVoiceCommand(String command) {
        String lowerCaseCommand = command.toLowerCase();

        // Check if the user is asking to set a reminder
        if (lowerCaseCommand.contains("remind me to")) {
            String reminderText = "";
            int index = lowerCaseCommand.indexOf("remind me to");
            reminderText = command.substring(index + "remind me to".length()).trim();

            if (reminderText.isEmpty()) {
                speakAndDisplay("What would you like to be reminded of?");
            } else {
                speakAndDisplay("Okay, opening reminders for: " + reminderText);
                Intent intent = new Intent(this, ReminderActivity.class);
                intent.putExtra(EXTRA_REMINDER_TEXT, reminderText);
                startActivity(intent);
            }
        } else {
            // Otherwise, send to AI for a general answer
            sendTextToAI(command);
        }
    }

    private void sendTextToAI(String userText) {
        binding.textView.append("\n\nThinking...");
        Content content = new Content.Builder().addText(userText).build();
        Executor mainExecutor = ContextCompat.getMainExecutor(this);

        backgroundExecutor.execute(() -> {
            try {
                GenerateContentResponse response = generativeModel.generateContent(content).get();
                String aiReply = response.getText();
                mainExecutor.execute(() -> speakAndDisplay(aiReply != null ? aiReply.trim() : "I didn't get a response."));
            } catch (Exception e) {
                mainExecutor.execute(() -> speakAndDisplay("AI Error: " + e.getMessage()));
            }
        });
    }

    private void speakAndDisplay(String text) {
        binding.textView.setText("AI: " + text);
        // --- DEFINITIVE FIX: Trigger the voice output ---
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AI_UTTERANCE");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}