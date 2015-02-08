package net.hogelab.android.androidui.musicreceiver;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.hogelab.android.androidui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kobayasi on 2015/01/20.
 */
public class MusicReceiverActivity extends ActionBarActivity {
    private static final String TAG = MusicReceiverActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_musicreceiver);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.musicreceiver_container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment
            implements MusicReceiver.MusicReceiverListener {
        private final String TAG = PlaceholderFragment.class.getSimpleName();


        private boolean mResumed;
        private MusicReceiver mReceiver;


        public PlaceholderFragment() {
            mResumed = false;
            mReceiver = new MusicReceiver(this);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mReceiver.startReceiver(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_musicreceiver, container, false);
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
        }

        @Override
        public void onPause() {
            super.onPause();

            mResumed = false;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            mReceiver.stopReceiver(getActivity());
        }


        @Override
        public void onReceiveMusicAction(String action, Bundle bundle) {
            List<String> items = new ArrayList<String>();

            items.add(action);

            for(String key : bundle.keySet()){
                Object obj = bundle.get(key);
                if (obj != null) {
                    String className = obj.getClass().getSimpleName();
                    String value = obj.toString();
                    String item = String.format(Locale.JAPANESE, "%s(%s) : %s", key, className, value);
                    items.add(item);
                } else {
                    String item = key + "(null)";
                    items.add(item);
                }
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_1,
                            items);

            setListAdapter(adapter);

            /*
            long id = bundle.getLong("id");
            String artist = bundle.getString("artist");
            String album = bundle.getString("album");
            String track = bundle.getString("track");
            Boolean playing = bundle.getBoolean("playing");
            long ListSize = bundle.getLong("ListSize");
            long duration = bundle.getLong("duration");
            long position = bundle.getLong("position");
            */
        }
    }
}
