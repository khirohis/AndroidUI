package net.hogelab.android.androidui.musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import net.hogelab.android.androidui.musicplayer.entity.Track;

import java.io.IOException;

/**
 * Created by kobayasi on 2015/01/21.
 */
public class PlayerService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = PlayerService.class.getSimpleName();


    public static final String ACTION_PLAY = "net.hogelab.android.androidui.action.PLAY";
    public static final String ACTION_STOP = "net.hogelab.android.androidui.action.STOP";
    public static final String ACTION_PAUSE = "net.hogelab.android.androidui.action.PAUSE";
    public static final String ACTION_TOGGLE_PLAYBACK = "net.hogelab.android.androidui.action.TOGGLE_PLAYBACK";
    public static final String ACTION_SKIP = "net.hogelab.android.androidui.action.SKIP";
    public static final String ACTION_REWIND = "net.hogelab.android.androidui.action.REWIND";

    public static final float DUCK_VOLUME = 0.1f;


    public class TestMusicPlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }


    private final IBinder mBinder = new TestMusicPlayerBinder();

    private AudioManager mAudioManager;

    private MediaPlayer mPlayer;
    private Track mTrackData;


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

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mPlayer = new MediaPlayer();
        initializePlayer(mPlayer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            cleanupPlayer(mPlayer);
            mPlayer = null;
        }

        super.onDestroy();
    }


    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory level:" + level);
    }


    // MediaPlayer Callbacks

    @Override
    public void onPrepared(MediaPlayer player) {
        Log.d(TAG, "onPrepared");

        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        Log.d(TAG, "onCompletion");

        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        Log.d(TAG, "onError");

        return false;
    }


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


    // play control

    public boolean startPlay(Track trackData) {
        boolean result = false;

        mTrackData = trackData;

        int focusRequestResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (focusRequestResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d(TAG, "startPlay:requestAudioFocus: not granted");
        }

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.reset();

        try {
            mPlayer.setDataSource(mTrackData.data);
            mPlayer.prepareAsync();

            result = true;
        } catch (IOException e) {
        }

        return result;
    }

    public void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void resume() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public void toggle() {
    }

    public void skip() {
    }

    public void rewind() {
        mPlayer.seekTo(0);
        mPlayer.start();
    }


    // private functions

    private void initializePlayer(MediaPlayer player) {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    private void cleanupPlayer(MediaPlayer player) {
        if (player.isPlaying()) {
            player.stop();
        }

        player.release();
    }


    private void onGainAudioFocus() {
        Log.d(TAG, "onGainAudioFocus");

        mPlayer.setVolume(1.0f, 1.0f);
    }

    private void onLossAudioFocus(boolean canDuck) {
        Log.d(TAG, "onLossAudioFocus: canDuck=" + canDuck);

        if (canDuck) {
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);
        } else {
            pause();
        }
    }
}
