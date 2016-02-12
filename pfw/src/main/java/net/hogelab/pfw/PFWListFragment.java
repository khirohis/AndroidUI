package net.hogelab.pfw;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kobayasi on 2016/02/12.
 */
public class PFWListFragment extends ListFragment {

    @Override
    public void onAttach(Context context) {
        PFWLog.v(this.getClass().getSimpleName(), "onAttach");

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PFWLog.v(this.getClass().getSimpleName(), "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PFWLog.v(this.getClass().getSimpleName(), "onCreateView");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        PFWLog.v(this.getClass().getSimpleName(), "onStart");

        super.onStart();
    }

    @Override
    public void onResume() {
        PFWLog.v(this.getClass().getSimpleName(), "onResume");

        super.onResume();
    }

    @Override
    public void onPause() {
        PFWLog.v(this.getClass().getSimpleName(), "onPause");

        super.onPause();
    }

    @Override
    public void onStop() {
        PFWLog.v(this.getClass().getSimpleName(), "onStop");

        super.onStop();
    }

    @Override
    public void onDestroy() {
        PFWLog.v(this.getClass().getSimpleName(), "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        PFWLog.v(this.getClass().getSimpleName(), "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        PFWLog.v(this.getClass().getSimpleName(), "onDetach");

        super.onDetach();
    }
}
