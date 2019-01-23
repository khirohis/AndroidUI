package net.hogelab.android.androidui.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SectionIndexer;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.entity.Album;
import net.hogelab.android.androidui.musicplayer.entity.Track;
import net.hogelab.pfw.PFWAppCompatActivity;
import net.hogelab.pfw.PFWListFragment;

/**
 * Created by hirohisa on 2015/01/22.
 */
public class MusicPlayerActivity extends PFWAppCompatActivity {
    private static final String TAG = MusicPlayerActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_musicreceiver);

        if (savedInstanceState == null) {
//            MusicPlayerAlbumFragment fragment = MusicPlayerAlbumFragment.newInstance();
            MusicPlayerAllTrackFragment fragment = MusicPlayerAllTrackFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.musicreceiver_container, fragment)
                    .commit();
        }
    }


    public static class MusicPlayerAlbumFragment extends PFWListFragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String TAG = MusicPlayerAlbumFragment.class.getSimpleName();


        private final int ALBUM_LOADER_ID = 0;
        private final String[] from = { MediaStore.Audio.Albums.ALBUM };
        private final int[] to = { android.R.id.text1 };

        private SimpleCursorAdapter mAdapter;


        public static MusicPlayerAlbumFragment newInstance() {
            return new MusicPlayerAlbumFragment();
        }


        public MusicPlayerAlbumFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_musicplayeralbum, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    null,
                    from,
                    to,
                    0);
            setListAdapter(mAdapter);

            getLoaderManager().initLoader(ALBUM_LOADER_ID, null, this);
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

            getLoaderManager().destroyLoader(ALBUM_LOADER_ID);
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "Item clicked: " + id);

            MusicPlayerTrackFragment fragment = MusicPlayerTrackFragment.newInstance(id);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.musicreceiver_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    Album.FIELDS_PROJECTION,
                    null,
                    null,
                    MediaStore.Audio.Albums.ALBUM + " COLLATE LOCALIZED ASC");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }
    }


    public static class MusicPlayerTrackFragment extends PFWListFragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String TAG = MusicPlayerTrackFragment.class.getSimpleName();


        private final int TRACK_LOADER_ID = 0;
        private final String[] from = { MediaStore.Audio.Media.TITLE };
        private final int[] to = { android.R.id.text1 };

        private SimpleCursorAdapter mAdapter;


        public static MusicPlayerTrackFragment newInstance(long albumId) {
            Bundle args = new Bundle();
            args.putLong("ALBUM_ID", albumId);

            MusicPlayerTrackFragment fragment = new MusicPlayerTrackFragment();
            fragment.setArguments(args);

            return fragment;
        }


        public MusicPlayerTrackFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_musicplayertrack, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    null,
                    from,
                    to,
                    0);
            setListAdapter(mAdapter);

            getLoaderManager().initLoader(TRACK_LOADER_ID, getArguments(), this);
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

            getLoaderManager().destroyLoader(TRACK_LOADER_ID);
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "Item clicked: " + id);

            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("TRACK_ID", id);
            startActivity(intent);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long albumId = args.getLong("ALBUM_ID");
            String[] selectionArgs = { Long.toString(albumId) };

            return new CursorLoader(getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Track.FIELDS_PROJECTION,
                    MediaStore.Audio.Media.ALBUM_ID + " = ?",
                    selectionArgs,
                    null);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }
    }


    public static class MusicPlayerAllTrackFragment extends PFWListFragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String TAG = MusicPlayerAllTrackFragment.class.getSimpleName();


        private final int ALLTRACK_LOADER_ID = 0;
        private final String[] from = { MediaStore.Audio.Media.TITLE };
        private final int[] to = { android.R.id.text1 };

        private AllTrackAdapter mAdapter;


        public static MusicPlayerAllTrackFragment newInstance() {
            MusicPlayerAllTrackFragment fragment = new MusicPlayerAllTrackFragment();

            return fragment;
        }


        public MusicPlayerAllTrackFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_musicplayertrack, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mAdapter = new AllTrackAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    null,
                    from,
                    to,
                    0);
            setListAdapter(mAdapter);
            getListView().setFastScrollEnabled(true);

            getLoaderManager().initLoader(ALLTRACK_LOADER_ID, getArguments(), this);
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

            getLoaderManager().destroyLoader(ALLTRACK_LOADER_ID);
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "Item clicked: " + id);

//            Intent intent = new Intent(getActivity(), PlayerActivity.class);
//            intent.putExtra("TRACK_ID", id);
//            startActivity(intent);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Track.FIELDS_PROJECTION,
                    null,
                    null,
                    MediaStore.Audio.Media.TITLE /* + " COLLATE NOCASE ASC" */);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }


        private static class AllTrackAdapter extends SimpleCursorAdapter implements SectionIndexer {

            private static final String TITLE_INITIAL = " #ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあかさたなはまやらわんアカサタナハマヤラワン漢";
            private static final String TITLE_INITIAL_IGNORECASE = " #ABCDEFGHIJKLMNOPQRSTUVWXYZあかさたなはまやらわんアカサタナハマヤラワン漢";

            private int titleIndex;
            private AlphabetIndexer alphabetIndexer;


            AllTrackAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
                super(context, layout, cursor, from, to, flags);
            }


            @Override
            public Cursor swapCursor(Cursor newCursor) {
                if (newCursor != null) {
                    if (alphabetIndexer != null) {
                        alphabetIndexer.setCursor(newCursor);
                    } else {
                        titleIndex = newCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                        alphabetIndexer = new AlphabetIndexer(newCursor, titleIndex, TITLE_INITIAL, false);
                    }
                }

                return super.swapCursor(newCursor);
            }


            @Override
            public Object[] getSections() {
                if (alphabetIndexer != null) {
                    return alphabetIndexer.getSections();
                }

                return new String[] { " " };
            }

            @Override
            public int getPositionForSection(int i) {
                if (alphabetIndexer != null) {
                    return alphabetIndexer.getPositionForSection(i);
                }

                return 0;
            }

            @Override
            public int getSectionForPosition(int i) {
                if (alphabetIndexer != null) {
                    return alphabetIndexer.getSectionForPosition(i);
                }

                return 0;
            }
        }
    }
}
