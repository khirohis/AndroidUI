package net.hogelab.android.androidui.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.entity.Track;

/**
 * Created by kobayasi on 2015/01/22.
 */
public class PlayerActivity extends ActionBarActivity {

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
    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor>,
            ServiceConnection {

        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private final int TRACK_LOADER_ID = 0;

        private boolean mPlayerServiceBound;
        private PlayerService mPlayerService;

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
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Intent intent = new Intent(getActivity(), PlayerService.class);
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

            PlayerService.TestMusicPlayerBinder binder = (PlayerService.TestMusicPlayerBinder)service;
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
    }
}
