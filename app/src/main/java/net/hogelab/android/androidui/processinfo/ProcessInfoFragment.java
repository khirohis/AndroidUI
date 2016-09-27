package net.hogelab.android.androidui.processinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kobayasi on 2016/08/16.
 */
public class ProcessInfoFragment extends PFWFragment
        implements LoaderManager.LoaderCallbacks<List<?>> {
    private final String TAG = ProcessInfoFragment.class.getSimpleName();


    private final int PROCESSINFO_LOADER_ID = 0;
    private final String LOADER_ARGUMENT_PREFIX = "prefix";
    private final String PREFERENCE_PREFIX = "prefix";

    private final long REFRESH_TIMER_INTERVAL = 10 * 1000;

    private boolean mResumed;
    private String mCurrentPrefix;
    private ProcessInfoAdapter mAdapter;
    private Handler mHandler;
    private Timer mTimer;

    private RecyclerView mRecyclerView;
    private EditText mEditNamePrefix;
    private Button mForwardMatch;



    public ProcessInfoFragment() {
        mResumed = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCurrentPrefix = preferences.getString(PREFERENCE_PREFIX, "");

        getLoaderManager().initLoader(PROCESSINFO_LOADER_ID, createLoaderParam(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_processinfo, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_processinfo);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEditNamePrefix = (EditText) rootView.findViewById(R.id.edit_name_prefix);
        mEditNamePrefix.setText(mCurrentPrefix);

        mForwardMatch = (Button) rootView.findViewById(R.id.button_forward_match);
        mForwardMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForwardMatchClicked();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mResumed = true;

        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();

        mResumed = false;

        stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getLoaderManager().destroyLoader(PROCESSINFO_LOADER_ID);
    }


    @Override
    public Loader<List<?>> onCreateLoader(int id, final Bundle args) {
        Log.d(TAG, "onCreateLoader");

        Loader<List<?>> loader = new AsyncTaskLoader<List<?>>(getActivity()) {

            @Override
            public List<?> loadInBackground() {
                String prefix = args.getString(LOADER_ARGUMENT_PREFIX);

                List<ActivityManager.RunningAppProcessInfo> result = new ArrayList<ActivityManager.RunningAppProcessInfo>();
                ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : runningApps) {
                    if (prefix == null || info.processName.startsWith(prefix)) {
                        result.add(info);
                    }
                }

                return result;
            }
        };

        loader.forceLoad();

        return loader;
    }

    @Override
    public void onLoaderReset(Loader<List<?>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onLoadFinished(Loader<List<?>> loader, final List<?> data) {
        Log.d(TAG, "onLoadFinished");

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                resetData(data);
            }
        });
    }


    private void resetData(List<?> deliverData) {
        Log.d(TAG, "resetData");

        if (mResumed) {
            mAdapter = new ProcessInfoAdapter(getActivity(), deliverData);
            mRecyclerView.setAdapter(mAdapter);

            startTimer();

            Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
        }
    }


    private void startTimer() {
        Log.d(TAG, "startTimer");

        stopTimer();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimer();
            }
        }, REFRESH_TIMER_INTERVAL);
    }

    private void stopTimer() {
        Log.d(TAG, "stopTimer");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();

            mTimer = null;
        }
    }

    private void onTimer() {
        Log.d(TAG, "onTimer");

        getLoaderManager().restartLoader(PROCESSINFO_LOADER_ID, createLoaderParam(), this);
    }


    private void onForwardMatchClicked() {
        Log.d(TAG, "onForwardMatchClicked");

        stopTimer();

        mCurrentPrefix = mEditNamePrefix.getText().toString();
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_PREFIX, mCurrentPrefix);
        editor.apply();

        getLoaderManager().restartLoader(PROCESSINFO_LOADER_ID, createLoaderParam(), this);
    }

    private Bundle createLoaderParam() {
        Bundle result = new Bundle();
        if (mCurrentPrefix != null) {
            result.putString(LOADER_ARGUMENT_PREFIX, mCurrentPrefix);
        }

        return result;
    }
}
