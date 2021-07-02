package com.playroomtv.exoplayerdemo.utilities;

public class MediaConst {
    /**
     * Media starts with this url
     * but if click KeyEvent.KEYCODE_DPAD_UP or KeyEvent.KEYCODE_DPAD_DOWN,
     * player continue with default media
     * go to {@link com.playroomtv.exoplayerdemo.MainActivity#dispatchKeyEvent}
     * to see default media url.
     */
    public static final String MEDIA_URL = "udp://@239.1.10.5:1234";
}
