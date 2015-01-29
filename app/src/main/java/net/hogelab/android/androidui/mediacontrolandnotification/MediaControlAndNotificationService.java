package net.hogelab.android.androidui.mediacontrolandnotification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by kobayasi on 2015/01/27.
 */
public class MediaControlAndNotificationService extends Service
        implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MediaControlAndNotificationService.class.getSimpleName();


    private static final int MEDIA_STYLE_NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_ACTION = 1;


    public class MediaStyleNotificationBinder extends Binder {
        public MediaControlAndNotificationService getService() {
            return MediaControlAndNotificationService.this;
        }
    }


    private final IBinder mBinder = new MediaStyleNotificationBinder();

    private ControlAndNotificationManager mManager;

    private int mPlayerState;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mPlayerState = PlayerState.INITIALIZED;

        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mManager = new ControlAndNotificationManager(this);
        mManager.startControl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PlayingInfo info = createPlayingInfo(true);
        mManager.setMetadata(info);
        mManager.setPlayState(ControlAndNotificationManager.PlayState.PLAYSTATE_PLAYING, info);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.abandonAudioFocus(this);

        mManager.stopControl();

        super.onDestroy();
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {

            case AudioManager.AUDIOFOCUS_GAIN:
                onGainAudioFocus();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                onLossAudioFocus(true);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                onLossAudioFocus(false);
                break;

            default:
                break;
        }
    }


    private void handleAction(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Log.d(TAG, "onStartCommand: Action=" + action);

            switch (action) {

                case PlayerAction.PLAY:
                    play();
                    break;

                case PlayerAction.STOP:
                    stop();
                    break;

                case PlayerAction.PAUSE:
                    pause();
                    break;

                case PlayerAction.TOGGLE_PLAY_PAUSE:
                    togglePlayPause();
                    break;

                case PlayerAction.SKIP_TO_NEXT:
                    skipToNext();
                    break;

                case PlayerAction.SKIP_TO_PREVIOUS:
                    skipToPrevious();
                    break;

                case PlayerAction.FAST_FORWARD:
                    break;

                case PlayerAction.REWIND:
                    break;

                case PlayerAction.SEEK_TO:
                    break;
            }
        } else {
            Log.d(TAG, "onStartCommand: Action=<null>");
        }
    }


    // Player Control

    private void play() {
        int newState;
        switch (mPlayerState) {

            case PlayerState.NONE:
            case PlayerState.STOPPED:
            case PlayerState.PAUSED:
                mPlayerState = PlayerState.PLAYING;
                break;

            case PlayerState.BUFFERING:
            case PlayerState.SKIPPING_TO_NEXT:
            case PlayerState.SKIPPING_TO_PREVIOUS:
                Log.d(TAG, "PLayerState Busy");
                break;

            case PlayerState.INITIALIZED:
            case PlayerState.ERROR:
                Log.d(TAG, "PLayerState Error");
                break;

            case PlayerState.PLAYING:
            default:
                break;
        }
    }

    private void stop() {
        switch (mPlayerState) {

            case PlayerState.BUFFERING:
            case PlayerState.PLAYING:
            case PlayerState.PAUSED:
            case PlayerState.SKIPPING_TO_NEXT:
            case PlayerState.SKIPPING_TO_PREVIOUS:
                mPlayerState = PlayerState.STOPPED;
                break;

            case PlayerState.INITIALIZED:
            case PlayerState.ERROR:
                Log.d(TAG, "PLayerState Error");
                break;

            case PlayerState.NONE:
            case PlayerState.STOPPED:
            default:
                break;
        }
    }

    private void pause() {
        switch (mPlayerState) {

            case PlayerState.PLAYING:
                mPlayerState = PlayerState.PAUSED;
                break;

            case PlayerState.INITIALIZED:
            case PlayerState.ERROR:
                Log.d(TAG, "PLayerState Error");
                break;

            case PlayerState.NONE:
            case PlayerState.BUFFERING:
            case PlayerState.STOPPED:
            case PlayerState.PAUSED:
            case PlayerState.SKIPPING_TO_NEXT:
            case PlayerState.SKIPPING_TO_PREVIOUS:
            default:
                break;
        }
    }

    private void togglePlayPause() {
        switch (mPlayerState) {

            case PlayerState.PLAYING:
                mPlayerState = PlayerState.PAUSED;
                break;

            case PlayerState.PAUSED:
                mPlayerState = PlayerState.PLAYING;
                break;

            case PlayerState.INITIALIZED:
            case PlayerState.ERROR:
                Log.d(TAG, "PLayerState Error");
                break;

            case PlayerState.NONE:
            case PlayerState.BUFFERING:
            case PlayerState.STOPPED:
            case PlayerState.SKIPPING_TO_NEXT:
            case PlayerState.SKIPPING_TO_PREVIOUS:
            default:
                break;
        }
    }

    private void skipToNext() {
    }

    private void skipToPrevious() {
    }


    // Audio Focus Control

    private void onGainAudioFocus() {
        Log.d(TAG, "onGainAudioFocus");
        // volume set to normal
    }

    private void onLossAudioFocus(boolean canDuck) {
        Log.d(TAG, "onLossAudioFocus: canDuck=" + canDuck);

        if (canDuck) {
            // volume set to lower
        } else {
            pause();
        }
    }


    private PlayingInfo createPlayingInfo(boolean isPlaying) {
        PlayingInfo info = new PlayingInfo();

        info.id = 1;

        info.data = "file:///sdcard/hoge";
        info.size = "32768";
        info.title = "Kuma Kuma Song";

        info.titleKey = "titleKey";
        info.artist = "KOBAHIRO";
        info.artistId = "1";
        info.artistKey = "artistKey";
        info.album = "All you need is KUMA";
        info.albumId = "1";
        info.albumKey = "albumKey";
        info.track = "1";
        info.duration = "60000";

        info.playing = isPlaying;
        info.listSize = 10;
        info.position = 0;
        info.isfavorite = false;
        info.shuffleMode = 0;
        info.repeatMode = 0;

        return info;
    }
}
