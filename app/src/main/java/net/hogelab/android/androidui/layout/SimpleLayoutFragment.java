package net.hogelab.android.androidui.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.hogelab.android.androidui.R;

/**
 * Created by hirohisa on 2015/02/26.
 */
public class SimpleLayoutFragment extends Fragment {
    private static final String TAG = SimpleLayoutFragment.class.getSimpleName();


    public static SimpleLayoutFragment newInstance() {
        SimpleLayoutFragment fragment = new SimpleLayoutFragment();

        return fragment;
    }


    private View mButtonContainer;


    public SimpleLayoutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simplelayout, container, false);
        return rootView;
    }

    public void onStart() {
        super.onStart();

        mButtonContainer = getActivity().findViewById(R.id.button_container);
        mButtonContainer.setVisibility(View.GONE);

        Button button = (Button)getActivity().findViewById(R.id.kuma_button2);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mButtonContainer.getVisibility() != View.VISIBLE) {
                    mButtonContainer.setVisibility(View.VISIBLE);
                } else {
                    mButtonContainer.setVisibility(View.GONE);
                }
            }
        });
    }
}