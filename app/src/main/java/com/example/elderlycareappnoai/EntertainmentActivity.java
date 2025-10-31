package com.example.elderlycareappnoai;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;

import com.example.elderlycareappnoai.databinding.ActivityEntertainmentBinding;

import java.util.Random;

public class EntertainmentActivity extends AppCompatActivity {

    private ActivityEntertainmentBinding binding;
    private String[] jokes = {
            "Why did the scarecrow win an award? Because he was outstanding in his field!",
            "What do you call cheese that isn’t yours? Nacho cheese!",
            "Why don’t skeletons fight each other? They don’t have the guts!"
    };

    private String[] facts = {
            "Honey never spoils. Archaeologists found 3,000-year-old honey still edible!",
            "Bananas are berries, but strawberries are not.",
            "Octopuses have three hearts and blue blood."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntertainmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Random random = new Random();

        // Show a random joke
        binding.jokeBtn.setOnClickListener(v -> {
            int index = random.nextInt(jokes.length);
            binding.resultText.setText("😂 " + jokes[index]);
            Toast.makeText(this, "Here's a joke!", Toast.LENGTH_SHORT).show();
        });

        // Show a random fact
        binding.factBtn.setOnClickListener(v -> {
            int index = random.nextInt(facts.length);
            binding.resultText.setText("💡 " + facts[index]);
            Toast.makeText(this, "Interesting fact!", Toast.LENGTH_SHORT).show();
        });

        // Back to main menu
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EntertainmentActivity.this, Maincode.class);
            startActivity(intent);
            finish();
        });
    }
}
