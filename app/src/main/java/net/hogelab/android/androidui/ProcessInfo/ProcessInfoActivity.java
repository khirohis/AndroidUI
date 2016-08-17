package net.hogelab.android.androidui.processinfo;

import android.os.Bundle;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWAppCompatActivity;

/**
 * Created by kobayasi on 2016/08/16.
 */
public class ProcessInfoActivity extends PFWAppCompatActivity {
    private static final String TAG = ProcessInfoActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_processinfo);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.processinfo_container, new ProcessInfoFragment())
                    .commit();
        }
    }
}
