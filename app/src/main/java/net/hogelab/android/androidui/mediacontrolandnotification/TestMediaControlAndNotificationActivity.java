package net.hogelab.android.androidui.mediacontrolandnotification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.R;

/**
 * Created by kobayasi on 2015/01/27.
 */
public class TestMediaControlAndNotificationActivity extends ActionBarActivity {

    private static final String TAG = TestMediaControlAndNotificationActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testmediacontrolandnotification);

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
    public static class PlaceholderFragment extends Fragment
            implements ServiceConnection {

        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private boolean mMediaStyleNotificationServiceBound;
        private MediaControlAndNotificationService mMediaStyleNotificationService;


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
            View rootView = inflater.inflate(R.layout.fragment_testmediacontrolandnotification, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Intent intent = new Intent(getActivity(), MediaControlAndNotificationService.class);
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

            MediaControlAndNotificationService.MediaStyleNotificationBinder binder =
                    (MediaControlAndNotificationService.MediaStyleNotificationBinder)service;
            mMediaStyleNotificationService = binder.getService();
            mMediaStyleNotificationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            mMediaStyleNotificationService = null;
            mMediaStyleNotificationServiceBound = false;
        }
    }
}
