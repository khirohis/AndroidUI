package net.hogelab.android.androidui.databinding;

import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingListItemViewModel {
    private static final String TAG = DataBindingListItemViewModel.class.getSimpleName();

    private String title;
    private String subtitle;
    private String thumbnailUrl;


    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Log.d(TAG, url);
        // ロードして

        // セットする
//        imageView.setImageBitmap(bitmap);
    }


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
}
