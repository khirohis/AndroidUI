package net.hogelab.android.androidui.uicatalog;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWFragment;

/**
 * Created by kobayasi on 2016/02/12.
 */
public class TabHostFragment extends PFWFragment
        implements TabHost.OnTabChangeListener, TabHost.TabContentFactory {

    private static final String TAG = TabHostFragment.class.getSimpleName();

    TabHost mTabHost;
    String mCurrentTag = null;


    enum TabTag {
        TypeA, TypeB, TypeC,
    };


    public static TabHostFragment newInstance() {
        TabHostFragment fragment = new TabHostFragment();

        return fragment;
    }


    public TabHostFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tabhost, container, false);

        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec(TabTag.TypeA.toString())
                .setIndicator(TabTag.TypeA.toString())
                .setContent(this));
        mTabHost.addTab(mTabHost.newTabSpec(TabTag.TypeB.toString())
                .setIndicator(TabTag.TypeB.toString())
                .setContent(this));
        mTabHost.addTab(mTabHost.newTabSpec(TabTag.TypeC.toString())
                .setIndicator(TabTag.TypeC.toString())
                .setContent(this));

        mTabHost.setOnTabChangedListener(this);

        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("tabtag");
            if (tag != null) {
                mTabHost.setCurrentTabByTag(tag);
            }
        } else {
            mCurrentTag = TabTag.TypeA.toString();
            mTabHost.setCurrentTabByTag(mCurrentTag);
        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        super.onSaveInstanceState(outState);

        if (mCurrentTag != null) {
            outState.putString("tabtag", mCurrentTag);
        }
    }


    @Override
    public void onTabChanged(String tag) {
        Log.d(TAG, "onTabChanged");

        if (mCurrentTag != tag) {
            Log.d(TAG, "tab " + mCurrentTag + " to " + tag);

            mCurrentTag = tag;
        }
    }

    @Override
    public View createTabContent(String tag) {
        final TextView textView = new TextView(getActivity());
        textView.setText(tag);

        return textView;
    }
}
