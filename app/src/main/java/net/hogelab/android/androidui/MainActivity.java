package net.hogelab.android.androidui;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.hogelab.android.androidui.loader.TestLoaderActivity;
import net.hogelab.android.androidui.mediacontrolandnotification.TestMediaControlAndNotificationActivity;
import net.hogelab.android.androidui.musicplayer.TestMusicPlayerActivity;
import net.hogelab.android.androidui.musicreceiver.TestMusicReceiverActivity;
import net.hogelab.android.androidui.navigationdrawer.TestNavigationDrawerActivity;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    private static final String[] items = {
            "Navigation Drawer",
            "Music Receiver",
            "Media Control & Notification",
            "Loader",
            "Music Player"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_1,
                            items);
            setListAdapter(adapter);
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {

                        case 0:
                            onSelectTestNavigationDrawer();
                            break;

                        case 1:
                            onSelectTestMusicReceiver();
                            break;

                        case 2:
                            onSelectTestMediaControlAndNotification();
                            break;

                        case 3:
                            onSelectTestLoader();
                            break;

                        case 4:
                            onSelectTestMusicPlayer();
                            break;

                        default:
                            break;
                    }
                }
            });
        }

        private void onSelectTestNavigationDrawer() {
            Intent intent = new Intent(getActivity(), TestNavigationDrawerActivity.class);
            startActivity(intent);
        }

        private void onSelectTestMusicReceiver() {
            Intent intent = new Intent(getActivity(), TestMusicReceiverActivity.class);
            startActivity(intent);
        }

        private void onSelectTestMediaControlAndNotification() {
            Intent intent = new Intent(getActivity(), TestMediaControlAndNotificationActivity.class);
            startActivity(intent);
        }

        private void onSelectTestLoader() {
            Intent intent = new Intent(getActivity(), TestLoaderActivity.class);
            startActivity(intent);
        }

        private void onSelectTestMusicPlayer() {
            Intent intent = new Intent(getActivity(), TestMusicPlayerActivity.class);
            startActivity(intent);
        }
    }
}
