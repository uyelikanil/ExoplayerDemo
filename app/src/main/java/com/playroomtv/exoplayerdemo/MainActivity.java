package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity implements Player.EventListener {

    PlayerView playerView;
    private SimpleExoPlayer player;

    int ip = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);
        initPlayer();
    }

    private void initPlayer(){
       // Set buffer settings
       DefaultLoadControl.Builder loadControl = new DefaultLoadControl.Builder()
               .setBufferDurationsMs(
                       50,
                       DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                       50,
                       50
               )
               .setBackBuffer(50,true);

        //Set track parameters
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(
                trackSelector
                        .buildUponParameters()
        .setForceLowestBitrate(true)
        );

        //Create the player
        player =  new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl.build())
                .setTrackSelector(trackSelector)
                .build();

        // set player in playerView
        playerView.setPlayer(player);
        playerView.requestFocus();
        // start play automatically when player is ready.
        player.setPlayWhenReady(true);

        changeChannel("udp://@239.1.1.1:1234");
    }

    // Changes media
    private void changeChannel(String url){
        if (url.isEmpty())
            Toast.makeText(this,"Please enter a url",Toast.LENGTH_SHORT).show();

        player.setMediaItem(MediaItem.fromUri(url));
        player.prepare();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(event.getKeyCode() == 19)
                ip=ip+1;
            else if(event.getKeyCode() == 20)
                ip=ip-1;

            changeChannel("udp://@239.1.1."+ip+":1234");
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(this, "onPlayerError: "+error.type, Toast.LENGTH_SHORT).show();
    }
}
