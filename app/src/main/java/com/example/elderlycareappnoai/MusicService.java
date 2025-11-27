package com.example.elderlycareappnoai; // Or your new package name

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We are not using binding, so return null
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            if (action.equals("PLAY")) {
                // If player is null, create it. If it's already playing, this does nothing.
                if (mediaPlayer == null) {
                    // Use default system ringtone or notification sound for simplicity
                    mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                    mediaPlayer.setLooping(true); // Loop the music
                }
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else if (action.equals("STOP")) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release(); // Release resources
                    mediaPlayer = null;
                }
            }
        }
        // If the service is killed, it will be automatically restarted
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Make sure to stop and release the media player when the service is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}