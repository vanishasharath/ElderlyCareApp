package com.example.elderlycareappnoai;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityChatBinding;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.chatView.setMovementMethod(new ScrollingMovementMethod());

        binding.sendButton.setOnClickListener(v -> {
            String userInput = binding.userInput.getText().toString().trim();
            if (userInput.isEmpty()) {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageToGemini(userInput);
        });
    }

    private void sendMessageToGemini(String userInput) {
        binding.chatView.append("You: " + userInput + "\n\n");
        binding.userInput.setText("");
        binding.sendButton.setEnabled(false);

        String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + userInput + "\" }] }] }";

        RequestBody body = RequestBody.create(
                jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + BuildConfig.GEMINI_API_KEY)
                .post(body)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String raw = response.body().string();

                if (response.isSuccessful()) {
                    String aiReply = parseGeminiResponse(raw);

                    runOnUiThread(() -> {
                        binding.chatView.append("AI: " + aiReply + "\n\n");
                        binding.sendButton.setEnabled(true);
                    });

                } else {
                    runOnUiThread(() -> {
                        binding.chatView.append("AI Error: " + response.message() + "\n\n");
                        binding.sendButton.setEnabled(true);
                    });
                }

            } catch (IOException e) {
                runOnUiThread(() -> {
                    binding.chatView.append("Exception: " + e.getMessage() + "\n\n");
                    binding.sendButton.setEnabled(true);
                });
            }
        }).start();
    }

    private String parseGeminiResponse(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray candidates = root.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            JSONObject textPart = parts.getJSONObject(0);

            return textPart.getString("text");
        } catch (Exception e) {
            Log.e("JSON_PARSE", "Error parsing", e);
            return "Error parsing AI response.";
        }
    }
}
