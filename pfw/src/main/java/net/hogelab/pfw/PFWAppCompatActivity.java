package net.hogelab.pfw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kobayasi on 2016/02/12.
 */
public class PFWAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PFWLog.v(this.getClass().getSimpleName(), "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        PFWLog.v(this.getClass().getSimpleName(), "onStart");

        super.onStart();
    }

    @Override
    public void onRestart() {
        PFWLog.v(this.getClass().getSimpleName(), "onRestart");

        super.onRestart();
    }

    @Override
    public void onResume() {
        PFWLog.v(this.getClass().getSimpleName(), "onResume");

        super.onResume();
    }

    @Override
    public void onPause() {
        PFWLog.v(this.getClass().getSimpleName(), "onPause");

        super.onPause();
    }

    @Override
    public void onStop() {
        PFWLog.v(this.getClass().getSimpleName(), "onStop");

        super.onStop();
    }

    @Override
    public void onDestroy() {
        PFWLog.v(this.getClass().getSimpleName(), "onDestroy");

        super.onDestroy();
    }
}
