package net.hogelab.android.androidui.musicplayer;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.entity.Album;

/**
 * Created by hirohisa on 2015/01/22.
 */
public class TestMusicPlayerActivity extends ActionBarActivity {
    private static final String TAG = TestMusicPlayerActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testmusicreceiver);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.testmusicreceiver_container, new TestMusicPlayerAlbumFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TestMusicPlayerAlbumFragment extends ListFragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String TAG = TestMusicPlayerAlbumFragment.class.getSimpleName();


        public TestMusicPlayerAlbumFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_testmusicplayeralbum, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getLoaderManager().initLoader(0, null, this);
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

            getLoaderManager().destroyLoader(0);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            return new CursorLoader(getActivity(),
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    Album.FIELDS,
                    null,
                    null,
                    MediaStore.Audio.Albums.ALBUM + "  ASC");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            CursorAdapter adapter = (CursorAdapter)getListAdapter();
            if (adapter != null) {
                Cursor oldCursor = adapter.swapCursor(null);
                if (oldCursor != null) {
                    oldCursor.close();
                }
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
            CursorAdapter adapter = (CursorAdapter)getListAdapter();
            if (adapter != null) {
                Cursor oldCursor = adapter.swapCursor(arg1);
                if (oldCursor != null) {
                    oldCursor.close();
                }
            } else {
                String[] from = { MediaStore.Audio.Albums.ALBUM };
                int[] to = { android.R.id.text1 };
                adapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,
                        arg1,
                        from,
                        to,
                        0);
                setListAdapter(adapter);
            }
        }
    }
}
