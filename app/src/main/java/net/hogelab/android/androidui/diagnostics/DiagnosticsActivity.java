package net.hogelab.android.androidui.diagnostics;

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

/**
 * Created by kobayasi on 2015/02/04.
 */
public class DiagnosticsActivity extends ActionBarActivity {

    private static final String TAG = DiagnosticsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diagnostics);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.diagnostics_container, new PlaceholderFragment())
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
            View rootView = inflater.inflate(R.layout.fragment_diagnosticslist, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_1,
                            DiagnosticsType.itemArray);
            setListAdapter(adapter);
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DiagnosticsResultFragment fragment = DiagnosticsResultFragment.newInstance(position);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.diagnostics_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
