package net.hogelab.android.androidui.layout;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.diagnostics.DiagnosticsResultFragment;

/**
 * Created by hirohisa on 2015/02/26.
 */
public class LayoutActivity extends ActionBarActivity {

    private static final String TAG = LayoutActivity.class.getSimpleName();

    private static final String[] items = {
            "simple",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_layout);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_container, new PlaceholderFragment())
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
            View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
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
                    SimpleLayoutFragment fragment = SimpleLayoutFragment.newInstance();

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
