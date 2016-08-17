package net.hogelab.android.androidui.mediacontrolandnotification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWAppCompatActivity;
import net.hogelab.pfw.PFWFragment;

/**
 * Created by kobayasi on 2015/01/27.
 */
public class MediaControlAndNotificationActivity extends PFWAppCompatActivity {

    private static final String TAG = MediaControlAndNotificationActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mediacontrolandnotification);

        if (savedInstanceState == null) {
            PlaceholderFragment fragment = PlaceholderFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mediastylenotification_container, fragment)
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends PFWFragment
            implements ServiceConnection {

        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private boolean mMediaStyleNotificationServiceBound;
        private PlayerMockService mMediaStyleNotificationService;


        public static PlaceholderFragment newInstance() {
            PlaceholderFragment fragment = new PlaceholderFragment();

            return fragment;
        }


        public PlaceholderFragment() {
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mediacontrolandnotification, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Intent intent = new Intent(getActivity(), PlayerMockService.class);
            getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
            getActivity().startService(intent);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            getActivity().unbindService(this);
        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");

            PlayerMockService.MediaStyleNotificationBinder binder =
                    (PlayerMockService.MediaStyleNotificationBinder)service;
            mMediaStyleNotificationService = binder.getService();
            mMediaStyleNotificationServiceBound = true;

            sendSetPlaylistAction();

            sendPlayAction();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            mMediaStyleNotificationService = null;
            mMediaStyleNotificationServiceBound = false;
        }


        private void sendSetPlaylistAction() {
            long[] playlist = { 1L, 2L, 3L, 4L, 5L};

            Intent intent = new Intent(getActivity(), PlayerMockService.class);
            intent.setAction(PlayerAction.SET_PLAYLIST);
            intent.putExtra(PlayerAction.EXTRA_KEY_PLAYLIST, playlist);
            getActivity().startService(intent);
        }

        private void sendPlayAction() {
            Intent intent = new Intent(getActivity(), PlayerMockService.class);
            intent.setAction(PlayerAction.PLAY);
            getActivity().startService(intent);
        }
    }
}
