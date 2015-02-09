package net.hogelab.android.androidui.diagnostics;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hogelab.android.androidui.R;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kobayasi on 2015/02/04.
 */
public class DiagnosticsResultFragment extends Fragment {
    private static final String TAG = DiagnosticsResultFragment.class.getSimpleName();


    public static DiagnosticsResultFragment newInstance(int diagnosticsTypeIndex) {
        Bundle args = new Bundle();
        args.putInt("DIAGNOSTICS_TYPE_INDEX", diagnosticsTypeIndex);

        DiagnosticsResultFragment fragment = new DiagnosticsResultFragment();
        fragment.setArguments(args);

        return fragment;
    }


    public DiagnosticsResultFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diagnosticsresult, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String infoText = null;

        Bundle args = getArguments();
        int diagnosticsTypeIndex = args.getInt("DIAGNOSTICS_TYPE_INDEX");
        switch (diagnosticsTypeIndex) {

            case 0:
                infoText = diagnosticsTypeDeviceInformation();
                break;

            case 1:
                infoText = diagnosticsTypeMemoryInformation();
                break;

            case 2:
                infoText = diagnosticsTypeAsyncTaskExecutor();
                break;

            default:
                break;
        }

        if (infoText != null) {
            TextView textView = (TextView) getActivity().findViewById(R.id.result_text);
            textView.setText(infoText);
        }
    }


    private String diagnosticsTypeDeviceInformation() {
        StringBuilder builder = new StringBuilder()
                .append("------------------------------\r\n")
                .append("Device Information\r\n")
                .append("  BRAND: " + android.os.Build.BRAND + "\r\n")
                .append("  DEVICE: " + android.os.Build.DEVICE + "\r\n")
                .append("  DISPLAY: " + android.os.Build.DISPLAY + "\r\n")
                .append("  MANUFACTURER: " + android.os.Build.MANUFACTURER + "\r\n")
                .append("  MODEL: " + android.os.Build.MODEL + "\r\n")
                .append("  PRODUCT: " + android.os.Build.PRODUCT + "\r\n")
                .append("  VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME + "\r\n")
                .append("  VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL + "\r\n")
                .append("  VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + "\r\n")
                .append("  VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT + "\r\n")
                .append("------------------------------");

        return builder.toString();
    }


    private String diagnosticsTypeMemoryInformation() {
        Runtime runtime = Runtime.getRuntime();

        StringBuilder builder = new StringBuilder()
                .append("------------------------------\r\n")
                .append("Memory Information\r\n")
                .append("  Total: " + runtime.totalMemory() + "\r\n")
                .append("  Free : " + runtime.freeMemory() + "\r\n")
                .append("  Total: " + (runtime.totalMemory() - runtime.freeMemory()) + "\r\n")
                .append("  Max  : " + runtime.maxMemory() + "\r\n")
                .append("------------------------------");

        return builder.toString();
    }


    @TargetApi(11)
    private String diagnosticsTypeAsyncTaskExecutor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;

            StringBuilder builder = new StringBuilder()
                    .append("------------------------------\r\n")
                    .append("AsyncTask.THREAD_POOL_EXECUTOR\r\n")
                    .append("  getActiveCount: " + executor.getActiveCount() + "\r\n")
                    .append("  getCompletedTaskCount: " + executor.getCompletedTaskCount() + "\r\n")
                    .append("  getCorePoolSize: " + executor.getCorePoolSize() + "\r\n")
                    .append("  getKeepAliveTime: " + executor.getKeepAliveTime(TimeUnit.MILLISECONDS) + "\r\n")
                    .append("  getLargestPoolSize: " + executor.getLargestPoolSize() + "\r\n")
                    .append("  getMaximumPoolSize: " + executor.getMaximumPoolSize() + "\r\n")
                    .append("  getPoolSize: " + executor.getPoolSize() + "\r\n")
                    .append("  getTaskCount: " + executor.getTaskCount() + "\r\n")
                    .append("------------------------------");

            return builder.toString();
        }

        return null;
    }
}
