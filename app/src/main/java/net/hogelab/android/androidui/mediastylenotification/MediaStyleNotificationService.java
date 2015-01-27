package net.hogelab.android.androidui.mediastylenotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import net.hogelab.android.androidui.R;

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

    private static final int MEDIA_STYLE_NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_ACTION = 1;


    public class MediaStyleNotificationBinder extends Binder {
        public MediaStyleNotificationService getService() {
            return MediaStyleNotificationService.this;
        }
    }


    private final IBinder mBinder = new MediaStyleNotificationBinder();


    private MediaSessionCompat mMediaSession;


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


    public void startMediaSession() {
        mMediaSession = new MediaSessionCompat(this, "Test Media Style Notification");
        mMediaSession.setCallback(new MediaSessionCallback(this));
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mMediaSession.setActive(true);
    }

    public void stopMediaSession() {
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    public void setMetadata() {
        mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Artist Title")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Album Title")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Track Title")
                .build());
    }

    @TargetApi(21)
    public void notifyMediaStyle() {
        MediaSession session = (MediaSession) mMediaSession.getMediaSession();
        Notification notification = new Notification.Builder(this)
                .setShowWhen(false)
                .setStyle(new Notification.MediaStyle()
                        .setMediaSession(session.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(0xFFCCCCCC)
//                .setLargeIcon(artwork)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Artist Title")
                .setContentInfo("Album Title")
                .setContentTitle("Track Title")
                .addAction(android.R.drawable.ic_media_previous, "Prev", createControlActionIntent(ACTION_REWIND))
                .addAction(android.R.drawable.ic_media_pause, "Pause", createControlActionIntent(ACTION_TOGGLE_PLAYBACK))
                .addAction(android.R.drawable.ic_media_next, "Next", createControlActionIntent(ACTION_SKIP))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(MEDIA_STYLE_NOTIFICATION_ID, notification);
    }

    public void cancelNotify() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(MEDIA_STYLE_NOTIFICATION_ID);
    }


    private PendingIntent createControlActionIntent(String action) {
        Intent intent = new Intent(action);
        ComponentName serviceName = new ComponentName(this, MediaStyleNotificationService.class);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, REQUEST_CODE_ACTION, intent, 0);
    }


    private static class MediaSessionCallback extends MediaSessionCompat.Callback {
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
