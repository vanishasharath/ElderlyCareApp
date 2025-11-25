package com.example.elderlycareappnoai;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.elderlycareappnoai.databinding.ActivityVoiceAssistantBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VoiceAssistantActivity extends AppCompatActivity {

    private ActivityVoiceAssistantBinding binding;
    private TextToSpeech tts;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private OkHttpClient client = new OkHttpClient();

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    granted -> {
                    });

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> text = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (text != null && !text.isEmpty()) {
                        String spoken = text.get(0);
                        binding.textView.setText("You said: " + spoken);
                        askGemini(spoken);
                    }
                } else {
                    binding.textView.setText("Sorry, I couldn’t understand. Try again.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        binding.startButton.setOnClickListener(v -> startListening());
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            try {
                speechLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Speech recognizer not available", Toast.LENGTH_LONG).show();
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }
    private void askGemini(String query) {
        executor.execute(() -> {
            try {
                JsonObject requestJson = new JsonObject();
                JsonArray contents = new JsonArray();
                JsonObject content = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject text = new JsonObject();

                text.addProperty("text", query);
                parts.add(text);
                content.add("parts", parts);
                contents.add(content);
                requestJson.add("contents", contents);

                Request request = new Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + BuildConfig.GEMINI_API_KEY)
                        .post(RequestBody.create(requestJson.toString(), MediaType.get("application/json; charset=utf-8")))
                        .build();

                Response response = client.newCall(request).execute();
                String body = response.body().string();

                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

                if (jsonObject.has("error")) {
                    String msg = jsonObject.getAsJsonObject("error").get("message").getAsString();
                    runOnUiThread(() -> binding.textView.setText("AI Error: " + msg));
                    return;
                }

                String reply = jsonObject.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();

                runOnUiThread(() -> {
                    binding.textView.setText("AI: " + reply);
                    tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
                });

            } catch (Exception e) {
                runOnUiThread(() -> binding.textView.setText("Exception: " + e.getMessage()));
            }
        });
        binding.stopButton.setOnClickListener(v -> {
            if (tts != null) {
                tts.stop();
                Toast.makeText(this, "Stopped speaking", Toast.LENGTH_SHORT).show();
            }
        });

    }
}