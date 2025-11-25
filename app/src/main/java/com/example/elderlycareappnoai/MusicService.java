package com.example.elderlycareappnoai;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    // Using default ringtone for simplicity
                    player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                    player.setLooping(true);
                    player.start();
                    break;
                case "STOP":
                    if (player != null && player.isPlaying()) {
                        player.stop();
                        player.release();
                        player = null;
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
        }
    }
}
