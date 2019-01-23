package net.hogelab.android.androidui.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.entity.Track;
import net.hogelab.pfw.PFWAppCompatActivity;
import net.hogelab.pfw.PFWFragment;

/**
 * Created by kobayasi on 2015/01/22.
 */
public class PlayerActivity extends PFWAppCompatActivity {

    private static final String TAG = PlayerActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        long track_id = getIntent().getLongExtra("TRACK_ID", -1);

        if (savedInstanceState == null) {
            PlaceholderFragment fragment = PlaceholderFragment.newInstance(track_id);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_container, fragment)
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends PFWFragment
            implements LoaderManager.LoaderCallbacks<Cursor>,
            ServiceConnection {

        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private final int TRACK_LOADER_ID = 0;

        private Handler mHandler = new Handler(Looper.getMainLooper());
        private ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                onVolumeChange(selfChange);
            }
        };

        private boolean mPlayerServiceBound;
        private PlayerService mPlayerService;

        private SeekBar mVolumeSeekBar;

        private Track mTrackData;


        public static PlaceholderFragment newInstance(long trackId) {
            Bundle args = new Bundle();
            args.putLong("TRACK_ID", trackId);

            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(args);

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
            View rootView = inflater.inflate(R.layout.fragment_player, container, false);
            // キー監視よりも Settings の ContentResolver のほうが確実
            rootView.setFocusableInTouchMode(true);
            rootView.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP) {
                        onVolumeChange(false);
                    }
                }

                return false;
            });

            mVolumeSeekBar = rootView.findViewById(R.id.volume_seek_bar);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Activity activity = getActivity();
            if (activity != null) {
//                activity.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mVolumeObserver);

                Intent intent = new Intent(activity, PlayerService.class);
                activity.bindService(intent, this, Context.BIND_AUTO_CREATE);
                activity.startService(intent);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            adjustVolumeSeekBar();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

            Activity activity = getActivity();
            if (activity != null) {
//                activity.getContentResolver().unregisterContentObserver(mVolumeObserver);

                activity.unbindService(this);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");

            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)service;
            mPlayerService = binder.getService();
            mPlayerServiceBound = true;

            getActivity().getSupportLoaderManager().initLoader(TRACK_LOADER_ID, getArguments(), this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            mPlayerService = null;
            mPlayerServiceBound = false;
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long trackId = args.getLong("TRACK_ID");
            String[] selectionArgs = { Long.toString(trackId) };

            return new CursorLoader(getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Track.FIELDS_PROJECTION,
                    MediaStore.Audio.Media._ID + " = ?",
                    selectionArgs,
                    null);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mTrackData = null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d(TAG, "onLoadFinished");

            if (data != null && data.moveToNext()) {
                mTrackData = new Track(data);
                if (mPlayerService != null) {
                    mPlayerService.startPlay(mTrackData);
                }
            }
        }


        private void onVolumeChange(boolean selfChange) {
            Log.d(TAG, "onVolumeChange selfChange: " + selfChange);

            if (!selfChange) {
                Runnable runnable = () -> adjustVolumeSeekBar();
                mHandler.post(runnable);
            }
        }

        private void adjustVolumeSeekBar() {
            Activity activity = getActivity();
            if (activity != null) {
                AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                mVolumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                mVolumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                Log.d(TAG, "AudioManager#getStreamVolume(STREAM_MUSIC) returned: " + mVolumeSeekBar.getProgress());
            }
        }
    }
}
