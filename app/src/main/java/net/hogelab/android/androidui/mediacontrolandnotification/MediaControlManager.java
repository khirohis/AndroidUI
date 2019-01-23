package net.hogelab.android.androidui.mediacontrolandnotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;

import net.hogelab.android.androidui.R;

/**
 * Created by hirohisa on 2015/01/28.
 */
public class MediaControlManager {
    private static final String TAG = MediaControlManager.class.getSimpleName();


    private static final int NOTIFICATION_ID = 1942;
    private static final int ACTION_REQUEST_CODE = 1;


    private Service mService;

    private NotificationManager mNotificationManager;
    private AudioManager mAudioManager;

    private boolean mSupportMediaSession;
    private boolean mSupportMediaStyleNotification;
    private boolean mSupportRemoteControlClient;
    private boolean mSupportMediaControlNotification;

    private MediaSessionCompat mMediaSession;
    private MediaSessionCompat.OnActiveChangeListener mActiveChangeListener;
    private ComponentName mMediaButtonReceiver;
    @SuppressWarnings("deprecation")
    private RemoteControlClient mRemoteControlClient;
    private Notification mNotification;


    public MediaControlManager(Service service) {
        mService = service;

        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) mService.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSupportMediaSession = true;
            mSupportMediaStyleNotification = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mSupportRemoteControlClient = true;
            mSupportMediaControlNotification = true;
        }
    }


    public void startControl() {
        if (mSupportMediaSession) {
            startMediaSession();
        }

        if (mSupportRemoteControlClient) {
            startRemoteControlClient();
        }

        updateNotification(PlaybackState.NONE, null);
    }

    public void stopControl() {
        if (mSupportMediaSession) {
            stopMediaSession();
        }

        if (mSupportRemoteControlClient) {
            stopRemoteControlClient();
        }

        cancelNotification();
    }


    public void setPlaybackState(int state, PlayingInfo info) {
        sendBroadcast(PlayerAction.PLAYSTATECHANGED, info);
        sendBroadcast(PlayerAction.SYSTEM_PLAYSTATECHANGED, info);

        if (mSupportMediaSession) {
            setPlaybackStateMediaSession(state);
        }

        if (mSupportRemoteControlClient) {
            setPlaybackStateRemoteControlClient(state);
        }

        updateNotification(state, info);
    }

    public void setMetadata(int state, PlayingInfo info) {
        sendBroadcast(PlayerAction.METACHANGED, info);
//        sendBroadcast(PlayerAction.SYSTEM_METACHANGED, info);

        if (mSupportMediaSession) {
            setMetadataMediaSession(info);
        }

        if (mSupportRemoteControlClient) {
            setMetadataRemoteControlClient(info);
        }

        updateNotification(state, info);
    }


    /**
     * broadcast
     */

    private void sendBroadcast(String action, PlayingInfo info) {
        Intent intent = new Intent(action);

        intent.putExtra("id", Long.valueOf(info.id));

        intent.putExtra("track", info.title);
        intent.putExtra("artist", info.artist);
        intent.putExtra("album", info.album);
        intent.putExtra("duration", Long.valueOf(info.duration));

        intent.putExtra("playing", Boolean.valueOf(info.playing));
        intent.putExtra("ListSize", Long.valueOf(info.listSize));
        intent.putExtra("position", Long.valueOf(info.position));
        intent.putExtra("isfavorite", Boolean.valueOf(info.isfavorite));
        intent.putExtra("shuffleMode", Integer.valueOf(info.shuffleMode));
        intent.putExtra("repeatMode", Integer.valueOf(info.repeatMode));

        mService.sendBroadcast(intent);
    }


    /**
     * MediaSession
     */

    private void startMediaSession() {
        if (mMediaSession == null) {
            ComponentName componentName = new ComponentName(mService, MediaSessionCallback.class);
            mMediaSession = new MediaSessionCompat(mService, "AndroidUI session", componentName, null);
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
            mMediaSession.setActive(true);

            if (mActiveChangeListener == null) {
                mActiveChangeListener = new MediaSessionCompat.OnActiveChangeListener() {
                    @Override
                    public void onActiveChanged() {
                        Log.d(TAG, "onActiveChanged MediaSession#isActive() = " + mMediaSession.isActive());
                    }
                };

                mMediaSession.addOnActiveChangeListener(mActiveChangeListener);
            }
        }
    }

    private void stopMediaSession() {
        if (mMediaSession != null) {
            if (mActiveChangeListener != null) {
                mMediaSession.removeOnActiveChangeListener(mActiveChangeListener);
                mActiveChangeListener = null;
            }

            mMediaSession.setActive(false);

            mMediaSession.release();
            mMediaSession = null;
        }
    }


    private void setPlaybackStateMediaSession(int state) {
        PlaybackStateCompat playbackState = null;
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();

        switch (state) {

            case PlaybackState.PLAYING:
                builder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f, SystemClock.elapsedRealtime());
                builder.setActions(PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO);
                playbackState = builder.build();
                break;

            case PlaybackState.PAUSED:
                builder.setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f, SystemClock.elapsedRealtime());
                builder.setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO);
                playbackState = builder.build();
                break;

            case PlaybackState.STOPPED:
                builder.setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f, SystemClock.elapsedRealtime());
                builder.setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
                playbackState = builder.build();
                break;

            default:
                break;
        }

        if (playbackState != null) {
            mMediaSession.setPlaybackState(playbackState);
        }
    }

    private void setMetadataMediaSession(PlayingInfo info) {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, info.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.valueOf(info.duration));

        mMediaSession.setMetadata(builder.build());
    }


    /**
     * RemoteControlClient
     */

    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void startRemoteControlClient() {
        mMediaButtonReceiver = new ComponentName(
                mService,
                MediaButtonEventReceiver.class);
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiver);

        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(mMediaButtonReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mService, 0, intent, 0);

        mRemoteControlClient = new RemoteControlClient(pendingIntent);
        mAudioManager.registerRemoteControlClient(mRemoteControlClient);

        mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                RemoteControlClient.FLAG_KEY_MEDIA_STOP);
    }

    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void stopRemoteControlClient() {
        mAudioManager.unregisterMediaButtonEventReceiver(mMediaButtonReceiver);
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
    }


    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void setPlaybackStateRemoteControlClient(int state) {
        switch (state) {

            case PlaybackState.PLAYING:
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                break;

            case PlaybackState.PAUSED:
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                break;

            case PlaybackState.STOPPED:
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
                break;

            default:
                break;
        }
    }

    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void setMetadataRemoteControlClient(PlayingInfo info) {
        mRemoteControlClient.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, info.title)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, info.album)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, info.artist)
                .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, Long.valueOf(info.duration))
                .apply();
    }


    /**
     * Notification
     */

    private void updateNotification(int state, PlayingInfo info) {
        if (mSupportMediaStyleNotification) {
            notifyMediaStyle(state, info);
        } else if (mSupportMediaControlNotification) {
            notifyMediaControl(state, info);
        } else {
            notifyMediaInfo(info);
        }
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);

        mNotification = null;
    }


    private void notifyMediaInfo(PlayingInfo info) {
        Intent intent = new Intent(mService, MediaControlAndNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mService, 0, intent, 0);

        RemoteViews contentView = new RemoteViews(mService.getPackageName(), R.layout.remoteviews_mediainfo);
        if (info != null) {
            contentView.setTextViewText(R.id.media_info_artist_album_text, info.artist + " / " + info.album);
            contentView.setTextViewText(R.id.media_info_title_text, info.title);
        } else {
            contentView.setTextViewText(R.id.media_control_artist_album_text, "-- / --");
            contentView.setTextViewText(R.id.media_control_title_text, "----");
        }

        mNotification = new NotificationCompat.Builder(mService)
                .setWhen(0)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContent(contentView)
                .build();

//        mNotification = new Notification();
//        mNotification.icon = R.drawable.ic_launcher;
//        mNotification.contentIntent = pendingIntent;
//        mNotification.contentView = contentView;

        mService.startForeground(NOTIFICATION_ID, mNotification);
    }

    private void notifyMediaControl(int state, PlayingInfo info) {
        Intent intent = new Intent(mService, MediaControlAndNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mService, 0, intent, 0);

        RemoteViews contentView = new RemoteViews(mService.getPackageName(), R.layout.remoteviews_mediacontrol);
        if (info != null) {
            contentView.setTextViewText(R.id.media_control_artist_album_text, info.artist + " / " + info.album);
            contentView.setTextViewText(R.id.media_control_title_text, info.title);
        } else {
            contentView.setTextViewText(R.id.media_control_artist_album_text, "-- / --");
            contentView.setTextViewText(R.id.media_control_title_text, "----");
        }

        if (state == PlaybackState.PLAYING) {
            contentView.setImageViewResource(R.id.toggle_button, android.R.drawable.ic_media_pause);
            contentView.setOnClickPendingIntent(R.id.toggle_button, createControlActionIntent(PlayerAction.PAUSE));
        } else {
            contentView.setImageViewResource(R.id.toggle_button, android.R.drawable.ic_media_play);
            contentView.setOnClickPendingIntent(R.id.toggle_button, createControlActionIntent(PlayerAction.PLAY));
        }
        contentView.setOnClickPendingIntent(R.id.skip_to_next_button, createControlActionIntent(PlayerAction.SKIP_TO_NEXT));
        contentView.setOnClickPendingIntent(R.id.skip_to_previous_button, createControlActionIntent(PlayerAction.SKIP_TO_PREVIOUS));

        mNotification = new NotificationCompat.Builder(mService)
                .setWhen(0)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContent(contentView)
                .build();

        mService.startForeground(NOTIFICATION_ID, mNotification);
    }

    @TargetApi(21)
    private void notifyMediaStyle(int state, PlayingInfo info) {
        PendingIntent pendingIntent = createControlActionIntent(PlayerAction.STOP);

        android.support.v4.media.app.NotificationCompat.MediaStyle style = new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mMediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService);
        builder.setShowWhen(false)
                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(artwork)
                .setColor(0xFF66CCCC)
                .setDeleteIntent(pendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setStyle(style);

        if (info != null) {
            builder.setContentText(info.artist + " / " + info.album);
            builder.setContentTitle(info.title);
        } else {
            builder.setContentText("-- / --");
            builder.setContentTitle("----");
        }

        builder.addAction(createControlAction(android.R.drawable.ic_media_previous, "Previous", PlayerAction.SKIP_TO_PREVIOUS));
        if (state == PlaybackState.PLAYING) {
            builder.addAction(createControlAction(android.R.drawable.ic_media_pause, "Pause", PlayerAction.PAUSE));
        } else {
            builder.addAction(createControlAction(android.R.drawable.ic_media_play, "Play", PlayerAction.PLAY));
        }
        builder.addAction(createControlAction(android.R.drawable.ic_media_next, "Next", PlayerAction.SKIP_TO_NEXT));

        mNotification = builder.build();

        mService.startForeground(NOTIFICATION_ID, mNotification);
    }


    private PendingIntent createControlActionIntent(String action) {
        Intent intent = new Intent(action);

        return PendingIntent.getService(mService, ACTION_REQUEST_CODE, intent, 0);
    }

    @TargetApi(21)
    private NotificationCompat.Action createControlAction(int resourceId, String title, String action) {
        PendingIntent intent = createControlActionIntent(action);

        return new NotificationCompat.Action.Builder(resourceId, title, intent).build();
    }



    public static class MediaSessionCallback extends MediaSessionCompat.Callback {
        private static final String TAG = MediaSessionCallback.class.getSimpleName();


        private Service mService;

        public MediaSessionCallback(Service service) {
            mService = service;
        }


        @Override
        public void onPlay() {
            super.onPlay();

            mService.startService(new Intent(PlayerAction.PLAY));
        }

        @Override
        public void onPause() {
            super.onPause();

            mService.startService(new Intent(PlayerAction.PAUSE));
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();

            mService.startService(new Intent(PlayerAction.SKIP_TO_NEXT));
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();

            mService.startService(new Intent(PlayerAction.SKIP_TO_PREVIOUS));
        }

        @Override
        public void onFastForward() {
            super.onFastForward();

            mService.startService(new Intent(PlayerAction.FAST_FORWARD));
        }

        @Override
        public void onRewind() {
            super.onRewind();

            mService.startService(new Intent(PlayerAction.REWIND));
        }

        @Override
        public void onStop() {
            super.onStop();

            mService.startService(new Intent(PlayerAction.STOP));
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);

            Intent intent = new Intent(PlayerAction.SEEK_TO);
            intent.putExtra(PlayerAction.EXTRA_KEY_POSITION, pos);
        }
    }


    public static class MediaButtonEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");

            String action = intent.getAction();
            if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                context.startService(newPlayerActionIntent(context, PlayerAction.PAUSE));
                return;
            } else if (!action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                return;
            }

            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
                return;
            }

            int keyCode = keyEvent.getKeyCode();
            switch (keyCode) {

                case KeyEvent.KEYCODE_HEADSETHOOK:
                    context.startService(newPlayerActionIntent(context, PlayerAction.TOGGLE_PLAY_PAUSE));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    context.startService(newPlayerActionIntent(context, PlayerAction.TOGGLE_PLAY_PAUSE));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    context.startService(newPlayerActionIntent(context, PlayerAction.PLAY));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    context.startService(newPlayerActionIntent(context, PlayerAction.PAUSE));
                    break;

                case KeyEvent.KEYCODE_MEDIA_STOP:
                    context.startService(newPlayerActionIntent(context, PlayerAction.STOP));
                    break;

                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    context.startService(newPlayerActionIntent(context, PlayerAction.SKIP_TO_NEXT));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    context.startService(newPlayerActionIntent(context, PlayerAction.SKIP_TO_PREVIOUS));
                    break;
            }
        }


        private Intent newPlayerActionIntent(Context context, String action) {
            Intent intent = new Intent(context, PlayerMockService.class);
            intent.setAction(action);

            return intent;
        }
    }
}
