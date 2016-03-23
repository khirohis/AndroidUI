package net.hogelab.android.androidui.databinding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWFragment;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingFragment extends PFWFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_binding, container, false);
        return rootView;
    }
}
