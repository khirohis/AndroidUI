package net.hogelab.android.androidui.musicplayer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;

import net.hogelab.android.androidui.musicplayer.entity.Track;

/**
 * Created by kobayasi on 2015/01/23.
 */
public class PlayerControlManager {
    private static final String TAG = PlayerControlManager.class.getSimpleName();


    private PlayerService mPlayerService;

    private RemoteControlClient mRemoteControlClient;


    public PlayerControlManager(PlayerService service) {
        mPlayerService = service;
    }

    public void startControl() {
        startControlBeforeLollipop();
    }

    public void setMetadata(Track track) {
        mRemoteControlClient.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, track.album)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, track.artist)
                .apply();
    }


    private void startControlBeforeLollipop() {
        AudioManager audioManager = (AudioManager) mPlayerService.getSystemService(Context.AUDIO_SERVICE);

        ComponentName receiver = new ComponentName(
                mPlayerService.getPackageName(),
                RemoteControlEventReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiver);

        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(receiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mPlayerService.getApplicationContext(), 0, intent, 0);

        mRemoteControlClient = new RemoteControlClient(pendingIntent);
        audioManager.registerRemoteControlClient(mRemoteControlClient);

        mRemoteControlClient.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                        RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                        RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                        RemoteControlClient.FLAG_KEY_MEDIA_STOP);
    }

    private void startControlAfterLollipop() {
    }


    public class RemoteControlEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }
}
