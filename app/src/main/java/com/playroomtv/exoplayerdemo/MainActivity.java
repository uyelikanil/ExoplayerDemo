package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.UdpDataSource;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.extractor.ts.TsExtractor.MODE_SINGLE_PMT;

public class MainActivity extends AppCompatActivity {

    PlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);
        initPlayer("udp://@239.1.3.3:1234");
    }

    private void initPlayer(String url){
        if (url.isEmpty())
            Toast.makeText(this,"Please enter a url",Toast.LENGTH_SHORT).show();
        Uri videoUri = Uri.parse(url);

        //Create a default TrackSelector
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());

        //Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        // set player in playerView
        playerView.setPlayer(player);
        playerView.requestFocus();

        //Create default UDP Datasource
        DataSource.Factory factory = () -> new UdpDataSource(3000, 100000);
        ExtractorsFactory tsExtractorFactory = () -> new TsExtractor[]{new TsExtractor(MODE_SINGLE_PMT,
                new TimestampAdjuster(0), new DefaultTsPayloadReaderFactory())};
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, factory, tsExtractorFactory,null,null);
        player.prepare(mediaSource);

        // start play automatically when player is ready.
        player.setPlayWhenReady(true);
    }
}
