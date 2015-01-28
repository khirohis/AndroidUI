package net.hogelab.android.androidui.mediacontrolandnotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import net.hogelab.android.androidui.R;

/**
 * Created by hirohisa on 2015/01/28.
 */
public class ControlAndNotificationManager {
    private static final String TAG = ControlAndNotificationManager.class.getSimpleName();


    private static final int MEDIA_STYLE_NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_ACTION = 1;

    public enum PlayState {
        PLAYSTATE_PLAYING, PLAYSTATE_PAUSED, PLAYSTATE_STOPPED
    };


    private Context mContext;

    private boolean mHasMediaSession;
    private boolean mHasRemoteControlClient;

    private MediaSessionCompat mMediaSession;

    private ComponentName mMediaButtonReceiver;
    @SuppressWarnings("deprecation")
    private RemoteControlClient mRemoteControlClient;


    public ControlAndNotificationManager(Context context) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHasMediaSession = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mHasRemoteControlClient = true;
        }
    }

    public void startControl() {
        if (mHasMediaSession) {
            startMediaSession();
        }

        if (mHasRemoteControlClient) {
            startRemoteControlClient();
        }
    }

    public void stopControl() {
        if (mHasMediaSession) {
            stopMediaSession();
        }

        if (mHasRemoteControlClient) {
            stopRemoteControlClient();
        }
    }

    public void setPlayState(PlayState state, PlayingInfo info) {
        sendBroadcast(PlayerAction.SYSTEM_PLAYSTATECHANGED, info);

        if (mHasMediaSession) {
            setPlayStateMediaSession(state);
        }

        if (mHasRemoteControlClient) {
            setPlayStateRemoteControlClient(state);
        }
    }

    public void setMetadata(PlayingInfo info) {
        sendBroadcast(PlayerAction.SYSTEM_METACHANGED, info);

        if (mHasMediaSession) {
            setMetadataMediaSession(info);
        }

        if (mHasRemoteControlClient) {
            setMetadataRemoteControlClient(info);
        }
    }


    private void sendBroadcast(String action, PlayingInfo info) {
        Intent intent = new Intent(action);

        intent.putExtra("id", Long.valueOf(info.id));
        intent.putExtra("track", info.title);
        intent.putExtra("artist", info.artist);
        intent.putExtra("album", info.album);
        intent.putExtra("playing", Boolean.valueOf(true));
        intent.putExtra("ListSize", Long.valueOf(1));
        intent.putExtra("duration", Long.valueOf(info.duration));
        intent.putExtra("position", Long.valueOf(1));
        intent.putExtra("isfavorite", Boolean.valueOf(false));
        intent.putExtra("shuffleMode", Integer.valueOf(0));
        intent.putExtra("repeatMode", Integer.valueOf(0));

        mContext.sendBroadcast(intent);
    }

    private PendingIntent createControlActionIntent(String action) {
        Intent intent = new Intent(action);
        ComponentName serviceName = new ComponentName(mContext, MediaControlAndNotificationService.class);
        intent.setComponent(serviceName);

        return PendingIntent.getService(mContext, REQUEST_CODE_ACTION, intent, 0);
    }

    @TargetApi(21)
    private Notification.Action createControlAction(int resourceId, String title, String action) {
        PendingIntent intent = createControlActionIntent(action);

        return new Notification.Action.Builder(resourceId, title, intent).build();
    }

    @TargetApi(21)
    private void notifyMediaStyle() {
        PendingIntent pendingIntent = createControlActionIntent(PlayerAction.STOP);

        MediaSession session = (MediaSession) mMediaSession.getMediaSession();
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setMediaSession(session.getSessionToken())
                .setShowActionsInCompactView(0);

        Notification.Builder builder = new Notification.Builder(mContext)
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

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MEDIA_STYLE_NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MEDIA_STYLE_NOTIFICATION_ID);

        mContext.startService(new Intent(PlayerAction.STOP));
    }


    private void startMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, "test session");
        mMediaSession.setCallback(new MediaSessionCallback(this));
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mMediaSession.setActive(true);

        // TODO: 違うところで
        notifyMediaStyle();
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
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.duration);

        mMediaSession.setMetadata(builder.build());
    }


    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void startRemoteControlClient() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mMediaButtonReceiver = new ComponentName(
                mContext,
                MediaButtonEventReceiver.class);
        audioManager.registerMediaButtonEventReceiver(mMediaButtonReceiver);

        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(mMediaButtonReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        mRemoteControlClient = new RemoteControlClient(pendingIntent);
        audioManager.registerRemoteControlClient(mRemoteControlClient);

        mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                RemoteControlClient.FLAG_KEY_MEDIA_STOP);
    }

    @TargetApi(14)
    @SuppressWarnings("deprecation")
    private void stopRemoteControlClient() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        audioManager.unregisterMediaButtonEventReceiver(mMediaButtonReceiver);
        audioManager.unregisterRemoteControlClient(mRemoteControlClient);
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
    private void setMetadataRemoteControlClient(PlayingInfo info) {
        mRemoteControlClient.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, info.title)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, info.album)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, info.artist)
                .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, info.duration)
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
