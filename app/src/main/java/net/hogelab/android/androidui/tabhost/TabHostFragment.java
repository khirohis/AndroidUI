package net.hogelab.android.androidui.tabhost;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import net.hogelab.android.androidui.R;

/**
 * Created by kobayasi on 2016/02/09.
 */
public class TabHostFragment extends Fragment implements TabHost.OnTabChangeListener, TabHost.TabContentFactory {
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
        Log.d(TAG, "onCreateView");

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

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            Bundle args = new Bundle();
            args.putString("type", mCurrentTag);
            Fragment fragment = TabContentFragment.newInstance(args);
            ft.add(R.id.realtabcontent, fragment, mCurrentTag);

            ft.commit();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");

        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
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

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment;

            if (mCurrentTag != null) {
                fragment = getFragmentManager().findFragmentByTag(mCurrentTag);
                if (fragment != null) {
                    ft.detach(fragment);
//                    ft.remove(fragment);
                }
            }

            fragment = getFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                ft.attach(fragment);
            } else {
                Bundle args = new Bundle();
                args.putString("type", tag);
                fragment = TabContentFragment.newInstance(args);
                ft.add(R.id.realtabcontent, fragment, tag);
            }

            ft.commit();

            mCurrentTag = tag;
        }
    }

    @Override
    public View createTabContent(String tag) {
        final TextView textView = new TextView(getActivity());
        textView.setText(tag);

        return textView;
    }


    public static class TabContentFragment extends Fragment {

        public static TabContentFragment newInstance(Bundle args) {
            TabContentFragment fragment = new TabContentFragment();
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(TAG, "TabContentFragment#onCreateView");

            View rootView = inflater.inflate(R.layout.fragment_tabcontent, container, false);

            Bundle args = getArguments();
            String type = args.getString("type");
            if (type != null) {
                TextView textView = (TextView) rootView.findViewById(R.id.textViewType);
                if (textView != null) {
                    textView.setText(type);
                }
            }

            return rootView;
        }
    }
}
