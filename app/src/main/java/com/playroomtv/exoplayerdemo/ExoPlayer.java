package com.playroomtv.exoplayerdemo;

import android.content.Context;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class ExoPlayer implements AnalyticsListener {
    private final Context context;
    public SimpleExoPlayer exoPlayer;
    public PlayerView playerView;

    public ExoPlayer(Context context, PlayerView playerView) {
        this.context = context;
        this.playerView = playerView;
    }

    /**
     * Creates player
     */
    private void createPlayer() {
        // Set buffer settings
        DefaultLoadControl.Builder loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                        DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                        500,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .setBackBuffer(DefaultLoadControl.DEFAULT_BACK_BUFFER_DURATION_MS, false);

        // Set track parameters
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        trackSelector.setParameters(
                trackSelector
                        .buildUponParameters()
        );

        // Set default renderer
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context);
        defaultRenderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        // Create the player
        exoPlayer =  new SimpleExoPlayer.Builder(context, defaultRenderersFactory)
                .setLoadControl(loadControl.build())
                .setTrackSelector(trackSelector)
                .setSkipSilenceEnabled(false)
                .setUseLazyPreparation(false)
                .setReleaseTimeoutMs(250)
                .setDetachSurfaceTimeoutMs(0)
                .build();
        exoPlayer.addAnalyticsListener(this);
        exoPlayer.setPlayWhenReady(true);
        playerView.setPlayer(exoPlayer);
    }

    /**
     * Changes media
     *
     * @param url Url of the media
     */
    public void changeMedia(String url){
        if (url.isEmpty()) {
            Toast.makeText(context, "Please enter a url", Toast.LENGTH_SHORT).show();
            return;
        }

        // Release player
        releasePlayer();
        // Clear surface
        clearPlayerSurface();
        // Create player
        createPlayer();
        // Set media
        setMedia(url);
    }

    /**
     * Sets media
     *
     * @param url Url of the media
     */
    private void setMedia(String url) {
        if(url != null) {
            MediaItem mediaItem =
                    new MediaItem.Builder()
                            .setUri(url)
                            .build();
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    /**
     * Restarts media
     */
    public void restartCurrentMedia() {
        if(exoPlayer.getCurrentMediaItem() != null) {
            Uri uri = exoPlayer.getCurrentMediaItem().playbackProperties.uri;
            exoPlayer.stop();
            playerView.setKeepContentOnPlayerReset(true);
            setMedia(uri.toString());
        }
    }

    /**
     * Clears player's surface
     */
    private void clearPlayerSurface() {
        if(playerView != null)
            clearSurface(playerView);
    }

    /**
     * Clears video view's surface
     */
    private void clearSurface(PlayerView playerView) {
        SurfaceView surfaceView = (SurfaceView) playerView.getVideoSurfaceView();
        Surface surface = surfaceView.getHolder().getSurface();
        if(!surface.isValid())
            return;

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, null);

        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, attribList, configs, configs.length, numConfigs);
        EGLConfig config = configs[0];
        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        });
        EGLSurface eglSurface = egl.eglCreateWindowSurface(display, config, surfaceView.getHolder().getSurface(),
                new int[]{
                        EGL14.EGL_NONE
                });

        egl.eglMakeCurrent(display, eglSurface, eglSurface, context);
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        egl.eglSwapBuffers(display, eglSurface);
        egl.eglDestroySurface(display, eglSurface);
        egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroyContext(display, context);
        egl.eglTerminate(display);
    }

    /**
     * Releases video view's surface
     */
    public void releasePlayer() {
        if(exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    @Override
    public void onPlaybackStateChanged(EventTime eventTime, int state) {
        String stateString;
        switch (state) {
            case com.google.android.exoplayer2.ExoPlayer.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                break;
            case com.google.android.exoplayer2.ExoPlayer.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                break;
            case com.google.android.exoplayer2.ExoPlayer.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                break;
            case com.google.android.exoplayer2.ExoPlayer.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";
                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }
        Log.e("ExoplayerState", "changed state to " + stateString);
    }

    @Override
    public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
        Log.e("ExoplayerState", "changed state to " + (isPlaying ? "PLAYING" : "STOPPED"));
    }

    @Override
    public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
        Log.e("TAG", "onDroppedVideoFrames: "+exoPlayer.isCurrentWindowLive());
        if(exoPlayer.isCurrentWindowLive()) {
            restartCurrentMedia();
        }
    }

    @Override
    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        Log.e("TAG", "onAudioUnderrun: "+exoPlayer.isCurrentWindowLive());
        if(exoPlayer.isCurrentWindowLive()) {
            restartCurrentMedia();
        }
    }

    @Override
    public void onAudioCodecError(EventTime eventTime, Exception audioCodecError) {
        Log.e("TAG", "onAudioCodecError: ");
    }

    @Override
    public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        Log.e("TAG", "onLoadError: ");
    }

    @Override
    public void onVideoDisabled(EventTime eventTime, DecoderCounters counters) {
        Log.e("TAG", "onVideoDisabled: ");
    }

    @Override
    public void onAudioSinkError(EventTime eventTime, Exception audioSinkError) {
        Log.e("TAG", "onAudioSinkError: isPlaying = "+exoPlayer.isPlaying());
        Log.e("TAG", "onAudioSinkError: isCurrentWindowLive = "+
                exoPlayer.isCurrentWindowLive());

        if(exoPlayer.isCurrentWindowLive() && !exoPlayer.isPlaying()) {
            restartCurrentMedia();
        }
    }

    @Override
    public void onPlayerReleased(EventTime eventTime) {
        Log.e("TAG", "onPlayerReleased: ");
    }

    @Override
    public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e("TAG", "onLoadCompleted: ");
    }

    @Override
    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
        Log.e("TAG", "onPlayerError: "+error.getMessage());
        Toast.makeText(context, "onPlayerError: "+error.getMessage(), Toast.LENGTH_SHORT).show();
        // TODO: control is live
        // TODO: check it for codec error
        restartCurrentMedia();
    }
}
