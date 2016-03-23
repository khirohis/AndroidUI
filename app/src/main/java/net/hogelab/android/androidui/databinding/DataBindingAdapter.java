package net.hogelab.android.androidui.databinding;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import net.hogelab.android.androidui.databinding.ListItemDataBindingBinding;
import net.hogelab.android.androidui.R;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingAdapter extends CursorAdapter {

    private LayoutInflater mInflater;


    public DataBindingAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ListItemDataBindingBinding binding = ListItemDataBindingBinding.inflate(mInflater);
        View view = binding.getRoot();
        view.setTag(binding);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ListItemDataBindingBinding binding = (ListItemDataBindingBinding) view.getTag();
        binding.setViewModel(new DataBindingListItemViewModel(cursor));
    }
}
