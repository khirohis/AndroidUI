package net.hogelab.android.androidui.mediacontrolandnotification;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by kobayasi on 2015/01/27.
 */
public class MediaControlAndNotificationService extends Service {
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

        PlayingInfo info = new PlayingInfo();
        mManager.setPlayState(ControlAndNotificationManager.PlayState.PLAYSTATE_PLAYING, info);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mManager.stopControl();

        super.onDestroy();
    }
}
