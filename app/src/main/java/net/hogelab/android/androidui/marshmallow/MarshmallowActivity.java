package net.hogelab.android.androidui.marshmallow;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWAppCompatActivity;

/**
 * Created by kobayasi on 2015/12/28.
 */
public class MarshmallowActivity extends PFWAppCompatActivity {

    private static final String TAG = MarshmallowActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marshmallow);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_container, new MarshmallowFragment())
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
}
