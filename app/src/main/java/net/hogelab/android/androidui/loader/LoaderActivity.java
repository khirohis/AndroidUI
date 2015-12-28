package net.hogelab.android.androidui.loader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hogelab.android.androidui.R;

/**
 * Created by hirohisa on 2015/02/02.
 */
public class LoaderActivity extends AppCompatActivity {
    private static final String TAG = LoaderActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loader);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.loader_container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<String> {
        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private final int TEST_LOADER_ID = 0;


        private boolean mResumed;


        public PlaceholderFragment() {
            mResumed = false;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_loader, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getLoaderManager().initLoader(TEST_LOADER_ID, null, this);

            (new AsyncTask<Void, Void, Void>() {

                @Override
                public Void doInBackground(Void... params) {
                    Log.d(TAG, "doInBackground");

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "doInBackground: return");

                    return null;
                }

            }).execute();
        }

        @Override
        public void onResume() {
            super.onResume();

            mResumed = true;
        }

        @Override
        public void onPause() {
            super.onPause();

            mResumed = false;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }


        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "onCreateLoader");

            Loader<String> loader = new AsyncTaskLoader<String>(getActivity()) {

                @Override
                public String loadInBackground() {
                    Log.d(TAG, "loadInBackground");

                    String result = "Kuma---!";

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "loadInBackground: return");

                    return result;
                }
            };

            loader.forceLoad();

            return loader;
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
            Log.d(TAG, "onLoaderReset");
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            Log.d(TAG, "onLoadFinished");

            final String deliverData = data;

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    MessageDialogFragment dialog = new MessageDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("message", deliverData);
                    dialog.setArguments(bundle);
                    dialog.show(getActivity().getSupportFragmentManager(), "message dialog");
                }
            });
        }


        public static class MessageDialogFragment extends DialogFragment {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                String message = getArguments().getString("message");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Load Completed")
                        .setMessage(message)
                        .setPositiveButton("OK", null);

                return builder.create();
            }
        }
    }
}
