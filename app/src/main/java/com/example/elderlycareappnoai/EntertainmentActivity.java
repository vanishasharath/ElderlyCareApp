package com.example.elderlycareappnoai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elderlycareappnoai.databinding.ActivityEntertainmentBinding;

import java.util.Random;

public class EntertainmentActivity extends AppCompatActivity {

    private ActivityEntertainmentBinding binding;
    private String[] jokes;
    private String[] facts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntertainmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load jokes and facts from resources
        jokes = getResources().getStringArray(R.array.jokes);
        facts = getResources().getStringArray(R.array.facts);

        Random random = new Random();

        // Joke button
        binding.jokeBtn.setOnClickListener(v -> {
            int index = random.nextInt(jokes.length);
            binding.resultText.setText(getString(R.string.joke_format, jokes[index]));
            Toast.makeText(this, "Here's a joke!", Toast.LENGTH_SHORT).show();
        });

        // Fact button
        binding.factBtn.setOnClickListener(v -> {
            int index = random.nextInt(facts.length);
            binding.resultText.setText(getString(R.string.fact_format, facts[index]));
            Toast.makeText(this, "Interesting fact!", Toast.LENGTH_SHORT).show();
        });

        // 🎵 Play music
        binding.playMusicBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction("PLAY");
            startService(intent);
            Toast.makeText(this, "Music started 🎶", Toast.LENGTH_SHORT).show();
        });

        // ⏹ Stop music
        binding.stopMusicBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction("STOP");
            startService(intent);
            Toast.makeText(this, "Music stopped ⛔", Toast.LENGTH_SHORT).show();
        });

        // Back button
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EntertainmentActivity.this, Maincode.class);
            startActivity(intent);
            finish();
        });
    }
}
