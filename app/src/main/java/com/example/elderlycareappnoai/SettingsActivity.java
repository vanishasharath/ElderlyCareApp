package com.example.elderlycareappnoai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Switch references
        Switch notificationSwitch = findViewById(R.id.notificationSwitch);

        Switch musicSwitch = findViewById(R.id.musicSwitch);
        SeekBar textSizeSeekBar = findViewById(R.id.textSizeSeekBar);
        TextView preview = findViewById(R.id.textSizePreview);
        Button backBtn = findViewById(R.id.backBtn);

        // Load saved settings
        notificationSwitch.setChecked(preferences.getBoolean("notifications", true));

        musicSwitch.setChecked(preferences.getBoolean("music", true));

        int savedSize = preferences.getInt("textSize", 16);
        textSizeSeekBar.setProgress(savedSize);
        preview.setTextSize(savedSize);

        // Save settings when toggled
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveBoolean("notifications", isChecked));

        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveBoolean("music", isChecked));


        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                preview.setTextSize(progress);
                saveInt("textSize", progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void saveBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    private void saveInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }
}
