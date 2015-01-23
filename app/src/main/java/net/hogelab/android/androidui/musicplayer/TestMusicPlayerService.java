package net.hogelab.android.androidui.musicplayer;

import android.app.Service;
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
public class TestMusicPlayerService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = TestMusicPlayerService.class.getSimpleName();


    public class TestMusicPlayerBinder extends Binder {
        public TestMusicPlayerService getService() {
            return TestMusicPlayerService.this;
        }
    }


    private final IBinder mBinder = new TestMusicPlayerBinder();
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

        mPlayer = new MediaPlayer();
        initializePlayer(mPlayer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        Log.d(TAG, "onError");

        return false;
    }


    // play control

    public boolean startPlay(Track trackData) {
        boolean result = false;

        mTrackData = trackData;

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }

        try {
            mPlayer.setDataSource(mTrackData.data);
            mPlayer.prepareAsync();

            result = true;
        } catch (IOException e) {
        }

        return result;
    }

    public void restart() {
        mPlayer.seekTo(0);
        mPlayer.start();
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
}
