package net.hogelab.android.androidui.databinding;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.databinding.FragmentDataBindingBinding;
import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.musicplayer.entity.Album;
import net.hogelab.pfw.PFWFragment;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingFragment extends PFWFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int ALBUM_LOADER_ID = 0;

    private FragmentDataBindingBinding mBinding;
    private DataBindingAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_binding, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding = FragmentDataBindingBinding.bind(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new DataBindingAdapter(getActivity(), null, false);
        mBinding.dataBindingList.setAdapter(mAdapter);

        getLoaderManager().initLoader(ALBUM_LOADER_ID, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getLoaderManager().destroyLoader(ALBUM_LOADER_ID);
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

        mAdapter.notifyDataSetChanged();
    }
}
