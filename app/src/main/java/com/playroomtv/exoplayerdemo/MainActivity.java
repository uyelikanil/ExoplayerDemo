package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity{
    private PlayerView playerView;
    private ExoPlayer player;
    private int ip = 1;
    private final String brokenMediaIp = "udp://239.101.101.106:1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);

        player = new ExoPlayer(this, playerView);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode() == 19) {
                if(ip > 8)
                    return false;
                ip = ip + 1;
                player.changeMedia("udp://@239.1.1."+ip+":1234");
            } else if(event.getKeyCode() == 20) {
                if(ip == 1)
                    return false;
                ip = ip - 1;
                player.changeMedia("udp://@239.1.1."+ip+":1234");
            }
            else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                if(player.exoPlayer.isPlaying())
                    player.exoPlayer.pause();
                else
                    player.exoPlayer.play();
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();

        player.changeMedia(brokenMediaIp);
    }

    @Override
    protected void onStop() {
        super.onStop();

        player.releasePlayer();
    }
}
