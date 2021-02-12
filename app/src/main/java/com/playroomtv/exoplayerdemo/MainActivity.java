package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity implements Player.EventListener {

    View shutter;
    PlayerView playerView;
    private SimpleExoPlayer player;

    int ip = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);
        shutter = findViewById(R.id.exo_shutter);

        initPlayer();
        changeChannel("udp://@239.1.1.1:1234");
    }

    private void initPlayer(){
       // Set buffer settings
       DefaultLoadControl.Builder loadControl = new DefaultLoadControl.Builder()
               .setBufferDurationsMs(
                       DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                       DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                       0,
                       DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
               )
               .setBackBuffer(DefaultLoadControl.DEFAULT_BACK_BUFFER_DURATION_MS,true);
        //Set track parameters
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(
                trackSelector
                        .buildUponParameters()
                .setExceedRendererCapabilitiesIfNecessary(true)
        );

        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        defaultRenderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        // Create the player
        player =  new SimpleExoPlayer.Builder(this,defaultRenderersFactory)
                .setLoadControl(loadControl.build())
                .setTrackSelector(trackSelector)
                .build();
        player.setPlayWhenReady(true);

        // set player in playerView
        playerView.setPlayer(player);
        playerView.requestFocus();
        playerView.setKeepContentOnPlayerReset(false);
    }

    // Changes media
    private void changeChannel(String url){
        if (url.isEmpty())
            Toast.makeText(this,"Please enter a url",Toast.LENGTH_SHORT).show();

        // Per MediaItem settings.
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(url)
                        .build();

        player.setMediaItem(mediaItem);
        player.prepare();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode() == 19) {
                if(ip > 8)
                    return false;
                ip = ip + 1;
                changeChannel("udp://@239.1.1."+ip+":1234");
            } else if(event.getKeyCode() == 20) {
                if(ip == 1)
                    return false;
                ip = ip - 1;
                changeChannel("udp://@239.1.1."+ip+":1234");
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(this, "onPlayerError: "+error.type, Toast.LENGTH_SHORT).show();
    }
}
