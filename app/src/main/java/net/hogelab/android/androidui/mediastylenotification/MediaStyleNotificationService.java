package net.hogelab.android.androidui.mediastylenotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.PlayerService;

/**
 * Created by kobayasi on 2015/01/27.
 */
public class MediaStyleNotificationService extends Service {
    private static final String TAG = MediaStyleNotificationService.class.getSimpleName();


    public static final String ACTION_PLAY = "net.hogelab.android.androidui.action.PLAY";
    public static final String ACTION_STOP = "net.hogelab.android.androidui.action.STOP";
    public static final String ACTION_PAUSE = "net.hogelab.android.androidui.action.PAUSE";
    public static final String ACTION_TOGGLE_PLAYBACK = "net.hogelab.android.androidui.action.TOGGLE_PLAYBACK";
    public static final String ACTION_SKIP = "net.hogelab.android.androidui.action.SKIP";
    public static final String ACTION_REWIND = "net.hogelab.android.androidui.action.REWIND";


    public class MediaStyleNotificationBinder extends Binder {
        public MediaStyleNotificationService getService() {
            return MediaStyleNotificationService.this;
        }
    }

    private static final int NOTIFICATION_ID = 1;

    private enum NOTIFICATION_ACTION_ID {
        NONE, PREVIOUS, PAUSE, NEXT
    };


    private final IBinder mBinder = new MediaStyleNotificationBinder();


    private MediaSession mMediaSession;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    @TargetApi(21)
    @Override
    public void onCreate() {
        super.onCreate();

        startMediaSession();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            Log.d(TAG, "onStartCommand: Action=" + action);
        }

        setMetadata();
        notifyMediaStyle();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopMediaSession();

        super.onDestroy();
    }

    @TargetApi(21)
    public void startMediaSession() {
        mMediaSession = new MediaSession(this, "Test Media Style Notification");
        mMediaSession.setCallback(new MediaSessionCallback(this));
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setActive(true);
    }

    @TargetApi(21)
    public void stopMediaSession() {
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    @TargetApi(21)
    public void setMetadata() {
        mMediaSession.setMetadata(new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artist Title")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "Album Title")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "Track Title")
                .build());
    }

    @TargetApi(21)
    public void notifyMediaStyle() {
        Notification notification = new Notification.Builder(this)
                .setShowWhen(false)
                .setStyle(new Notification.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(0xFFCCCCCC)
//                .setLargeIcon(artwork)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Artist Title")
                .setContentInfo("Album Title")
                .setContentTitle("Track Title")
                .addAction(android.R.drawable.ic_media_previous, "Prev", createAction(NOTIFICATION_ACTION_ID.PREVIOUS))
                .addAction(android.R.drawable.ic_media_pause, "Pause", createAction(NOTIFICATION_ACTION_ID.PAUSE))
                .addAction(android.R.drawable.ic_media_next, "Next", createAction(NOTIFICATION_ACTION_ID.NEXT))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void cancelNotify() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    private PendingIntent createAction(NOTIFICATION_ACTION_ID actionId) {
        PendingIntent pendingIntent = null;
        Intent action;
        ComponentName serviceName = new ComponentName(this, MediaStyleNotificationService.class);

        switch (actionId) {

            case PREVIOUS:
                action = new Intent(ACTION_TOGGLE_PLAYBACK);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, actionId.ordinal(), action, 0);
                break;

            case PAUSE:
                action = new Intent(ACTION_PAUSE);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, actionId.ordinal(), action, 0);
                break;

            case NEXT:
                action = new Intent(ACTION_SKIP);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, actionId.ordinal(), action, 0);
                break;
        }

        return pendingIntent;
    }


    @TargetApi(21)
    private static class MediaSessionCallback extends MediaSession.Callback {
        private static final String TAG = MediaSessionCallback.class.getSimpleName();


        private MediaStyleNotificationService mService;

        public MediaSessionCallback(MediaStyleNotificationService service) {
            mService = service;
        }


        @Override
        public void onPlay() {
            super.onPlay();

//            sendNotification(createControlAction(android.R.drawable.ic_media_pause, "Pause", PlayerService.ACTION_PAUSE));
        }

        @Override
        public void onPause() {
            super.onPause();

//            sendNotification(createControlAction(android.R.drawable.ic_media_play, "Play", PlayerService.ACTION_PLAY));
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
        }

        @Override
        public void onRewind() {
            super.onRewind();
        }

        @Override
        public void onStop() {
            super.onStop();

            mService.cancelNotify();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }
    }
}
