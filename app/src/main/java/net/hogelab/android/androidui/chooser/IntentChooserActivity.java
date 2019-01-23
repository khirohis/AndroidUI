package net.hogelab.android.androidui.chooser;

import android.os.Bundle;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWAppCompatActivity;

public class IntentChooserActivity extends PFWAppCompatActivity {
    private static final String TAG = IntentChooserActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intent_chooser);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.intent_chooser_container, new IntentChooserFragment())
                    .commit();
        }
    }
}
