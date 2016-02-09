package net.hogelab.android.androidui;

import android.app.Application;
import android.util.Log;

/**
 * Created by kobayasi on 2016/01/15.
 */
public class AndroidUiApplication extends Application {

    private static final String TAG = AndroidUiApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();
    }
}
