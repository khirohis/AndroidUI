package net.hogelab.pfw;

import android.util.Log;

/**
 * Created by kobayasi on 2016/02/12.
 */
public class PFWLog {

    public enum LogLevel {
        VERBOSE(2),
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6),
        ASSERT(7);

        private final int mLevel;

        LogLevel(int level) {
            mLevel = level;
        }

        public int getLevel() {
            return mLevel;
        }

        public int compare(LogLevel logLevel) {
            return mLevel - logLevel.mLevel;
        }
    }


    private static volatile LogLevel mLogLevel = LogLevel.VERBOSE;


    public LogLevel getLogLevel() {
        return mLogLevel;
    }

    public void setLogLevel(LogLevel level) {
        mLogLevel = level;
    }


    public static void v(String tag, String message) {
        logging(LogLevel.VERBOSE, tag, message);
    }

    public static void d(String tag, String message) {
        logging(LogLevel.DEBUG, tag, message);
    }

    public static void i(String tag, String message) {
        logging(LogLevel.INFO, tag, message);
    }

    public static void w(String tag, String message) {
        logging(LogLevel.WARN, tag, message);
    }

    public static void e(String tag, String message) {
        logging(LogLevel.ERROR, tag, message);
    }


    private static void logging(LogLevel requiredLogLevel, String tag, String message) {
        if (BuildConfig.DEBUG && mLogLevel.compare(requiredLogLevel) >= 0) {
            Log.v(tag, message);
        }
    }
}
