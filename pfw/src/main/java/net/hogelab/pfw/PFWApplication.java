package net.hogelab.pfw;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by kobayasi on 2016/02/12.
 */
public class PFWApplication extends Application {

    @Override
    public void onCreate() {
        PFWLog.v(this.getClass().getSimpleName(), "onCreate");

        super.onCreate();
    }

    @Override
    public void onTerminate() {
        PFWLog.v(this.getClass().getSimpleName(), "onTerminate");

        super.onTerminate();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        PFWLog.v(this.getClass().getSimpleName(), "onConfigurationChanged");

        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        PFWLog.v(this.getClass().getSimpleName(), "onLowMemory");

        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        PFWLog.v(this.getClass().getSimpleName(), "onTrimMemory");

        super.onTrimMemory(level);
    }
}
