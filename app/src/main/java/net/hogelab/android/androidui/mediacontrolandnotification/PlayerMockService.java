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
public class PlayerMockService extends Service
        implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = PlayerMockService.class.getSimpleName();


    private static final int MEDIA_STYLE_NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_ACTION = 1;


    public class MediaStyleNotificationBinder extends Binder {
        public PlayerMockService getService() {
            return PlayerMockService.this;
        }
    }


    private final IBinder mBinder = new MediaStyleNotificationBinder();

    private ControlAndNotificationManager mManager;

    private int mPlaybackState;

    private long[] mPlaylist;
    private int mPlaylistIndex;


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

        mPlaybackState = PlaybackState.INITIALIZED;

        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mManager = new ControlAndNotificationManager(this);
        mManager.startControl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleAction(intent);

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
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                onGainAudioFocus();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                onLossAudioFocus(false);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT");
                onLossAudioFocus(false);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                onLossAudioFocus(true);
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
                case PlayerAction.SET_PLAYLIST:
                    setPlaylist(intent);
                    break;

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

    private void setPlaybackStateAndNotify(int state, boolean playing) {
        mPlaybackState = state;

        PlayingInfo info = getPlayingInfo(playing);
        if (info != null) {
            mManager.setPlaybackState(PlaybackState.PLAYING, info);
        }
    }

    private void setPlaylist(Intent intent) {
        long[] playlist = intent.getLongArrayExtra(PlayerAction.EXTRA_KEY_PLAYLIST);
        if (playlist != null) {
            stop();

            mPlaylist = playlist;
            mPlaylistIndex = 0;

            mPlaybackState = PlaybackState.READY_TO_PLAY;

            PlayingInfo info = getPlayingInfo(false);
            mManager.setMetadata(info);
        }
    }

    private void play() {
        switch (mPlaybackState) {

            case PlaybackState.READY_TO_PLAY:
            case PlaybackState.STOPPED:
            case PlaybackState.PAUSED:
                mPlaybackState = PlaybackState.PLAYING;
                setPlaybackStateAndNotify(PlaybackState.PLAYING, true);
                break;

            default:
                Log.d(TAG, "play: not handled. PlaybackState=" + mPlaybackState);
                break;
        }
    }

    private void stop() {
        boolean handled = false;

        switch (mPlaybackState) {

            case PlaybackState.BUFFERING:
            case PlaybackState.PLAYING:
            case PlaybackState.PAUSED:
            case PlaybackState.SKIPPING_TO_NEXT:
            case PlaybackState.SKIPPING_TO_PREVIOUS:
                mPlaybackState = PlaybackState.STOPPED;
                setPlaybackStateAndNotify(PlaybackState.STOPPED, false);
                break;

            default:
                Log.d(TAG, "stop: not handled. PlaybackState=" + mPlaybackState);
                break;
        }
    }

    private void pause() {
        switch (mPlaybackState) {

            case PlaybackState.PLAYING:
                mPlaybackState = PlaybackState.PAUSED;
                setPlaybackStateAndNotify(PlaybackState.PAUSED, false);
                break;

            default:
                Log.d(TAG, "pause: not handled. PlaybackState=" + mPlaybackState);
                break;
        }
    }

    private void togglePlayPause() {
        switch (mPlaybackState) {

            case PlaybackState.PLAYING:
                mPlaybackState = PlaybackState.PAUSED;
                setPlaybackStateAndNotify(PlaybackState.PAUSED, false);
                break;

            case PlaybackState.PAUSED:
                mPlaybackState = PlaybackState.PLAYING;
                setPlaybackStateAndNotify(PlaybackState.PLAYING, true);
                break;

            default:
                Log.d(TAG, "togglePlayPause: not handled. current PlaybackState=" + mPlaybackState);
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


    private PlayingInfo getPlayingInfo(boolean isPlaying) {
        if (mPlaylist == null) {
            return null;
        }

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
        info.track = Integer.toString(mPlaylistIndex + 1);
        info.duration = "60000";

        info.playing = isPlaying;
        info.listSize = mPlaylist.length;
        info.position = 0;
        info.isfavorite = false;
        info.shuffleMode = 0;
        info.repeatMode = 0;

        return info;
    }
}
