package net.hogelab.android.androidui.databinding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by kobayasi on 2016/03/24.
 */
public final class DataBindingExtension {
    private static final String TAG = DataBindingExtension.class.getSimpleName();


    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            Log.d(TAG, imageUrl);

            Context context = imageView.getContext();
            if (context != null) {
                Glide.with(context).load(imageUrl).animate(android.R.anim.fade_in).into(imageView);
            }
        } else {
            // デフォルト画像をセットする
            imageView.setImageBitmap(null);
        }
    }
}
