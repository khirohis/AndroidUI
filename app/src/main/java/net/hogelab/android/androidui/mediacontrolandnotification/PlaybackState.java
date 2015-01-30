package net.hogelab.android.androidui.mediacontrolandnotification;

/**
 * Created by hirohisa on 2015/01/29.
 */
public class PlaybackState {
    public static final int NONE = 0;

    public static final int INITIALIZED = 1;
    public static final int READY_TO_PLAY = 2;

    public static final int BUFFERING = 3;
    public static final int PLAYING = 4;
    public static final int STOPPED = 5;
    public static final int PAUSED = 6;

    public static final int SKIPPING_TO_NEXT = 7;
    public static final int SKIPPING_TO_PREVIOUS = 8;

    public static final int ERROR = 100;
}
