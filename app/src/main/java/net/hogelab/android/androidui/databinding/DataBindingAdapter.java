package net.hogelab.android.androidui.databinding;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.databinding.ListItemDataBindingBinding;
import net.hogelab.android.androidui.databinding.viewmodel.DataBindingListItemViewModel;

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

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ListItemDataBindingBinding binding = DataBindingUtil.getBinding(view);
        if (binding != null) {
            // clear immediate
            binding.thumbnail.setImageDrawable(null);

            binding.setViewModel(new DataBindingListItemViewModel(view.getContext().getApplicationContext(), cursor));
        }
    }
}
