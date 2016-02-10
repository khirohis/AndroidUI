package net.hogelab.android.androidui;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.hogelab.android.androidui.diagnostics.DiagnosticsActivity;
import net.hogelab.android.androidui.layout.LayoutActivity;
import net.hogelab.android.androidui.loader.LoaderActivity;
import net.hogelab.android.androidui.marshmallow.MarshmallowActivity;
import net.hogelab.android.androidui.mediacontrolandnotification.MediaControlAndNotificationActivity;
import net.hogelab.android.androidui.musicplayer.MusicPlayerActivity;
import net.hogelab.android.androidui.musicreceiver.MusicReceiverActivity;
import net.hogelab.android.androidui.navigationdrawer.NavigationDrawerActivity;
import net.hogelab.android.androidui.tabhost.TabHostActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    private static final String[] items = {
            "Diagnostics",
            "Navigation Drawer",
            "Music Receiver",
            "Media Control & Notification",
            "Loader",
            "Layout",
            "Music Player",
            "Marshmallow",
            "TabHost",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

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
                            onSelectDiagnostics();
                            break;

                        case 1:
                            onSelectNavigationDrawer();
                            break;

                        case 2:
                            onSelectMusicReceiver();
                            break;

                        case 3:
                            onSelectMediaControlAndNotification();
                            break;

                        case 4:
                            onSelectLoader();
                            break;

                        case 5:
                            onSelectLayout();
                            break;

                        case 6:
                            onSelectMusicPlayer();
                            break;

                        case 7:
                            onSelectMarshmallow();
                            break;

                        case 8:
                            onSelectTabHost();
                            break;

                        default:
                            break;
                    }
                }
            });
        }

        private void onSelectDiagnostics() {
            Intent intent = new Intent(getActivity(), DiagnosticsActivity.class);
            startActivity(intent);
        }

        private void onSelectNavigationDrawer() {
            Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
            startActivity(intent);
        }

        private void onSelectMusicReceiver() {
            Intent intent = new Intent(getActivity(), MusicReceiverActivity.class);
            startActivity(intent);
        }

        private void onSelectMediaControlAndNotification() {
            Intent intent = new Intent(getActivity(), MediaControlAndNotificationActivity.class);
            startActivity(intent);
        }

        private void onSelectLoader() {
            Intent intent = new Intent(getActivity(), LoaderActivity.class);
            startActivity(intent);
        }

        private void onSelectLayout() {
            Intent intent = new Intent(getActivity(), LayoutActivity.class);
            startActivity(intent);
        }

        private void onSelectMusicPlayer() {
            Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
            startActivity(intent);
        }

        private void onSelectMarshmallow() {
            Intent intent = new Intent(getActivity(), MarshmallowActivity.class);
            startActivity(intent);
        }

        private void onSelectTabHost() {
            Intent intent = new Intent(getActivity(), TabHostActivity.class);
            startActivity(intent);
        }
    }
}
