package net.hogelab.android.androidui.databinding;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingListItemViewModel {
    private static final String TAG = DataBindingListItemViewModel.class.getSimpleName();

    private String title;
    private String subtitle;
    private String thumbnailUrl;


    public DataBindingListItemViewModel(Cursor cursor) {
        title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
        subtitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
        thumbnailUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
    }


    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


    public View.OnClickListener onClickListItem() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + title);
            }
        };
    }
}
