package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.playroomtv.exoplayerdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private ExoPlayer player;
    private int ip = 1;
    private final String brokenMediaIp = "udp://@239.1.10.5:1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        player = new ExoPlayer(this, binding.playerView);
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
                int playerState = player.exoPlayer.getPlaybackState();

                if(playerState == Player.STATE_READY  || playerState == Player.STATE_ENDED){
                    DialogFragment dialogFragment = new TrackSelectorDialog(this,
                            getSupportFragmentManager(), player.exoPlayer, player.trackSelector);
                    dialogFragment.show(getSupportFragmentManager(), "trackSelectorDialog");
                }
                else if(playerState == Player.STATE_BUFFERING) {
                    Toast.makeText(this, "Please wait media is loading",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                else if(playerState == Player.STATE_IDLE){
                    Toast.makeText(this, "There is no playable media",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
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
