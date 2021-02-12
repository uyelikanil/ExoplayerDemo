package com.playroomtv.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.video.DummySurface;
import com.google.android.exoplayer2.video.VideoListener;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

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

        changeChannel("udp://@239.1.1.1:1234");
    }

    // Changes media
    private void changeChannel(String url){
        if (url.isEmpty())
            Toast.makeText(this,"Please enter a url",Toast.LENGTH_SHORT).show();

        // Clear surface
        if(player!=null) {
            player.stop();
            player.release();
            clearSurface();
        }

        // Set buffer settings
        DefaultLoadControl.Builder loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                        DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                        0,
                        0
                )
                .setBackBuffer(0,false);
        //Set track parameters
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(
                trackSelector
                        .buildUponParameters()
                        .setExceedRendererCapabilitiesIfNecessary(true)
        );

        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        defaultRenderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        defaultRenderersFactory.setAllowedVideoJoiningTimeMs(0);

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

    /**
     * Clears video view's surface
     */
    public void clearSurface() {
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

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(this, "onPlayerError: "+error.type, Toast.LENGTH_SHORT).show();
    }
}
