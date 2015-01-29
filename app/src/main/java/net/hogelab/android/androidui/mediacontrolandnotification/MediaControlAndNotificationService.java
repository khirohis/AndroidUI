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

        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mManager = new ControlAndNotificationManager(this);
        mManager.startControl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            action = "<null>";
        }
        Log.d(TAG, "onStartCommand: Action=" + action);

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


    private void onGainAudioFocus() {
        Log.d(TAG, "onGainAudioFocus");
        // volume set to normal
    }

    private void onLossAudioFocus(boolean canDuck) {
        Log.d(TAG, "onLossAudioFocus: canDuck=" + canDuck);

        if (canDuck) {
            // volume set to lower
        } else {
            // pause play
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
