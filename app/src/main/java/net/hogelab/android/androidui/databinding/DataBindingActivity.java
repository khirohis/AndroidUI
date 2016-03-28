package net.hogelab.android.androidui.databinding;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.hogelab.android.androidui.databinding.ActivityDataBindingBinding;
import net.hogelab.android.androidui.R;
import net.hogelab.android.androidui.databinding.viewmodel.DataBindingRootViewModel;
import net.hogelab.pfw.PFWAppCompatActivity;

/**
 * Created by kobayasi on 2016/03/23.
 */
public class DataBindingActivity extends PFWAppCompatActivity {
    private static final String TAG = DataBindingActivity.class.getSimpleName();

    private ActivityDataBindingBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityDataBindingBinding.inflate(getLayoutInflater());
        mBinding.setViewModel(new DataBindingRootViewModel());
        setContentView(mBinding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.data_binding_container, new DataBindingFragment())
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
