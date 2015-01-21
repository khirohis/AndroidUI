package net.hogelab.android.androidui.musicreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kobayasi on 2015/01/20.
 */
public class TestMusicReceiver extends BroadcastReceiver {
    private static final String TAG = TestMusicReceiver.class.getSimpleName();

    public static final String ACTION_METACHANGED = "com.android.music.metachanged";
    public static final String ACTION_PLAYSTATECHANGED = "com.android.music.playstatechanged";
    public static final String ACTION_PLAYBACKCOMPLETE = "com.android.music.playbackcomplete";


    public interface TestMusicReceiverListener {
        public void onReceiveMusicAction(String action, Bundle bundle);
    }


    private boolean mRegistered;
    private TestMusicReceiverListener mListener;


    public TestMusicReceiver(TestMusicReceiverListener listener) {
        mRegistered = false;
        mListener = listener;
    }

    public void setListener(TestMusicReceiverListener listener) {
    }


    public void startReceiver(Context context) {
        if (!mRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_METACHANGED);
            intentFilter.addAction(ACTION_PLAYSTATECHANGED);
            intentFilter.addAction(ACTION_PLAYBACKCOMPLETE);
            context.registerReceiver(this, intentFilter);

            mRegistered = true;
        }
    }

    public void stopReceiver(Context context) {
        if (mRegistered) {
            context.unregisterReceiver(this);

            mRegistered = false;
        }
    }


    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        if (mListener != null) {
            mListener.onReceiveMusicAction(action, bundle);
        }
    }
}
