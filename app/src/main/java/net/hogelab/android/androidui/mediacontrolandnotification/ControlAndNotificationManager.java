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
import android.media.session.MediaSession;
import android.os.Build;
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
public class ControlAndNotificationManager {
    private static final String TAG = ControlAndNotificationManager.class.getSimpleName();


    private static final int REMOTE_VIEWS_NOTIFICATION_ID = 1;
    private static final int MEDIA_STYLE_NOTIFICATION_ID = 2;

    private static final int REQUEST_CODE_ACTION = 1;

    public enum PlayState {
        PLAYSTATE_PLAYING, PLAYSTATE_PAUSED, PLAYSTATE_STOPPED
    };


    private Service mService;
    private NotificationManager mNotificationManager;
    private AudioManager mAudioManager;

    private boolean mSupportMediaSession;
    private boolean mSupportMediaStyleNotification;
    private boolean mSupportRemoteControlClient;

    private Notification mRemoteViewsNotification;

    private MediaSessionCompat mMediaSession;

    private ComponentName mMediaButtonReceiver;
    @SuppressWarnings("deprecation")
    private RemoteControlClient mRemoteControlClient;


    public ControlAndNotificationManager(Service service) {
        mService = service;
        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) mService.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSupportMediaSession = true;
            mSupportMediaStyleNotification = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mSupportRemoteControlClient = true;
        }
    }

    public void startControl() {
        if (mSupportMediaSession) {
            startMediaSession();
        }

        // TODO: setPlayState の再生開始に移すこと
        if (mSupportMediaStyleNotification) {
            notifyMediaStyle();
        } else {
            notifyRemoteViews();
        }
        // ここまで

        if (mSupportRemoteControlClient) {
            startRemoteControlClient();
        }
    }

    public void stopControl() {
        if (mSupportMediaSession) {
            stopMediaSession();
        }

        if (mSupportRemoteControlClient) {
            stopRemoteControlClient();
        }
    }

    public void setPlayState(PlayState state, PlayingInfo info) {
        sendBroadcast(PlayerAction.SYSTEM_PLAYSTATECHANGED, info);

        if (mSupportMediaSession) {
            setPlayStateMediaSession(state);
        }

        if (!mSupportMediaStyleNotification) {
            setPlayStateRemoteViewsNotification(state);
        }

        if (mSupportRemoteControlClient) {
            setPlayStateRemoteControlClient(state);
        }
    }

    public void setMetadata(PlayingInfo info) {
        sendBroadcast(PlayerAction.SYSTEM_METACHANGED, info);

        if (mSupportMediaSession) {
            setMetadataMediaSession(info);
        }

        if (!mSupportMediaStyleNotification) {
            setMetadataRemoteViewsNotification(info);
        }

        if (mSupportRemoteControlClient) {
            setMetadataRemoteControlClient(info);
        }
    }


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


    private PendingIntent createControlActionIntent(String action) {
        Intent intent = new Intent(action);
        ComponentName serviceName = new ComponentName(mService, MediaControlAndNotificationService.class);
        intent.setComponent(serviceName);

        return PendingIntent.getService(mService, REQUEST_CODE_ACTION, intent, 0);
    }

    @TargetApi(21)
    private Notification.Action createControlAction(int resourceId, String title, String action) {
        PendingIntent intent = createControlActionIntent(action);

        return new Notification.Action.Builder(resourceId, title, intent).build();
    }


    private void notifyRemoteViews() {
        Intent intent = new Intent(mService, MediaControlAndNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mService, 0, intent, 0);

        RemoteViews contentView = new RemoteViews(mService.getPackageName(), R.layout.remoteviews_mediacontrolandnotification);
        contentView.setOnClickPendingIntent(R.id.toggle_button, createControlActionIntent(PlayerAction.PAUSE));

        mRemoteViewsNotification = new Notification();
        mRemoteViewsNotification.icon = R.drawable.ic_launcher;
        mRemoteViewsNotification.contentView = contentView;
        mRemoteViewsNotification.contentIntent = pendingIntent;

        mService.startForeground(REMOTE_VIEWS_NOTIFICATION_ID, mRemoteViewsNotification);
    }

    private void setPlayStateRemoteViewsNotification(PlayState state) {
        RemoteViews contentView = mRemoteViewsNotification.contentView;
//        contentView.setImageViewResource(R.id.toggle_button, toggleAction);
//        contentView.setOnClickPendingIntent(R.id.toggle_button, toggleAction);

        mService.startForeground(REMOTE_VIEWS_NOTIFICATION_ID, mRemoteViewsNotification);
    }

    private void setMetadataRemoteViewsNotification(PlayingInfo info) {
        RemoteViews contentView = mRemoteViewsNotification.contentView;
        contentView.setTextViewText(R.id.media_info, info.artist + " / " + info.album);
        contentView.setTextViewText(R.id.media_title, info.title);

        mService.startForeground(REMOTE_VIEWS_NOTIFICATION_ID, mRemoteViewsNotification);
    }


    @TargetApi(21)
    private void notifyMediaStyle() {
        PendingIntent pendingIntent = createControlActionIntent(PlayerAction.STOP);

        MediaSession session = (MediaSession) mMediaSession.getMediaSession();
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setMediaSession(session.getSessionToken())
                .setShowActionsInCompactView(0);

        Notification.Builder builder = new Notification.Builder(mService)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(artwork)
                .setColor(0xFFCCCCCC)
                .setContentTitle("Title")
                .setContentText("Artist")
                .setDeleteIntent(pendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setStyle(style);

//        builder.addAction(createControlAction( android.R.drawable.ic_media_previous, "Previous", PlayerService.ACTION_PREVIOUS));
        builder.addAction(createControlAction(android.R.drawable.ic_media_rew, "Rewind", PlayerAction.REWIND));
//        builder.addAction(createControlAction( android.R.drawable.ic_media_ff, "Fast Foward", PlayerService.ACTION_FAST_FORWARD));
//        builder.addAction(createControlAction( android.R.drawable.ic_media_next, "Next", PlayerService.ACTION_NEXT));

        mNotificationManager.notify(MEDIA_STYLE_NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        mNotificationManager.cancel(REMOTE_VIEWS_NOTIFICATION_ID);
        mNotificationManager.cancel(MEDIA_STYLE_NOTIFICATION_ID);

        // TODO: need?
        mService.startService(new Intent(PlayerAction.STOP));
    }


    private void startMediaSession() {
        mMediaSession = new MediaSessionCompat(mService, "test session");
        mMediaSession.setCallback(new MediaSessionCallback(this));
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mMediaSession.setActive(true);
    }

    private void stopMediaSession() {
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    private void setPlayStateMediaSession(PlayState state) {
        PlaybackStateCompat playbackState = null;
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();

        switch (state) {

            case PLAYSTATE_PLAYING:
                builder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f);
                builder.setActions(PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO);
                playbackState = builder.build();
                break;

            case PLAYSTATE_PAUSED:
                builder.setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f);
                builder.setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO);
                playbackState = builder.build();
                break;

            case PLAYSTATE_STOPPED:
                builder.setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f);
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
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.valueOf(info.duration));

        mMediaSession.setMetadata(builder.build());
    }


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
    private void setPlayStateRemoteControlClient(PlayState state) {
        switch (state) {

            case PLAYSTATE_PLAYING:
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                break;

            case PLAYSTATE_PAUSED:
                mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                break;

            case PLAYSTATE_STOPPED:
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


    private static class MediaSessionCallback extends MediaSessionCompat.Callback {
        private static final String TAG = MediaSessionCallback.class.getSimpleName();


        private ControlAndNotificationManager mManager;

        public MediaSessionCallback(ControlAndNotificationManager manager) {
            mManager = manager;
        }


        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
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

            // TODO: 違うところで
            mManager.cancelNotification();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }
    }


    public static class MediaButtonEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");

            String action = intent.getAction();
            if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                context.startService(new Intent(PlayerAction.PAUSE));
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
                    context.startService(new Intent(PlayerAction.TOGGLE_PLAYBACK));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    context.startService(new Intent(PlayerAction.TOGGLE_PLAYBACK));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    context.startService(new Intent(PlayerAction.PLAY));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    context.startService(new Intent(PlayerAction.PAUSE));
                    break;

                case KeyEvent.KEYCODE_MEDIA_STOP:
                    context.startService(new Intent(PlayerAction.STOP));
                    break;

                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    context.startService(new Intent(PlayerAction.SKIP));
                    break;

                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    context.startService(new Intent(PlayerAction.REWIND));
                    break;
            }
        }
    }
}
