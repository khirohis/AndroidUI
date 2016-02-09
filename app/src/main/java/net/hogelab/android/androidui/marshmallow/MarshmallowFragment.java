package net.hogelab.android.androidui.marshmallow;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.hogelab.android.androidui.R;


/**
 * Created by kobayasi on 2015/12/28.
 */
public class MarshmallowFragment extends Fragment {
    private static final String TAG = MarshmallowFragment.class.getSimpleName();

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;


    public static MarshmallowFragment newInstance() {
        MarshmallowFragment fragment = new MarshmallowFragment();

        return fragment;
    }


    public MarshmallowFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_marshmallow, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        TextView textView;
        Button button;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission;

            textView = (TextView) getActivity().findViewById(R.id.textViewIsMarshmallowText);
            textView.setText("Yes");

            textView = (TextView) getActivity().findViewById(R.id.textViewCanReadPhoneStateText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestReadPhoneState);
            hasPermission = PermissionChecker.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            if (hasPermission) {
                textView.setText("Yes");
                button.setVisibility(View.GONE);
            } else {
                textView.setText("No");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestReadPhoneState();
                    }
                });
            }

            textView = (TextView) getActivity().findViewById(R.id.textViewCanWriteExternalStorageText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestWriteExternalStorage);
            hasPermission = PermissionChecker.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (hasPermission) {
                textView.setText("Yes");
                button.setVisibility(View.GONE);
            } else {
                textView.setText("No");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestWriteExternalStorage();
                    }
                });
            }

            textView = (TextView) getActivity().findViewById(R.id.textViewIsIgnoringBatteryOptimizationsText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestIgnoringBatteryOptimizations);
            PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            hasPermission = powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName());
            if (hasPermission) {
                textView.setText("Yes");
                button.setVisibility(View.GONE);
            } else {
                textView.setText("No");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestIgnoringBatteryOptimizations();
                    }
                });
            }

        } else {
            textView = (TextView) getActivity().findViewById(R.id.textViewIsMarshmallowText);
            textView.setText("No");

            textView = (TextView) getActivity().findViewById(R.id.textViewCanReadPhoneStateText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestReadPhoneState);
            textView.setText("N/A");
            button.setVisibility(View.GONE);

            textView = (TextView) getActivity().findViewById(R.id.textViewCanWriteExternalStorageText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestWriteExternalStorage);
            textView.setText("N/A");
            button.setVisibility(View.GONE);

            textView = (TextView) getActivity().findViewById(R.id.textViewIsIgnoringBatteryOptimizationsText);
            button = (Button) getActivity().findViewById(R.id.buttonRequestIgnoringBatteryOptimizations);
            textView.setText("N/A");
            button.setVisibility(View.GONE);
        }

        String status = Environment.getExternalStorageState();
        Log.d(TAG, "Environment.getExternalStorageState returned: " + status);

        status = EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory());
        Log.d(TAG, "EnvironmentCompat.getStorageState returned: " + status);

        boolean canWrite = Environment.getExternalStorageDirectory().canWrite();
        Log.d(TAG, "EnvironmentCompat.getExternalStorageDirectory.canWrite returned: " + canWrite);

        if (!canWrite) {
            showRestartDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                Log.d(TAG, "REQUEST_READ_PHONE_STATE granted: " + isGranted);
                if (isGranted) {
                    Toast.makeText(getActivity(), "READ_PHONE_STATE is granted", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_WRITE_EXTERNAL_STORAGE:
                Log.d(TAG, "REQUEST_WRITE_EXTERNAL_STORAGE granted: " + isGranted);
                if (isGranted) {
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL_STORAGE is granted", Toast.LENGTH_LONG).show();

                    String status = EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory());
                    Log.d(TAG, "EnvironmentCompat.getStorageState returned: " + status);

                    boolean canWrite = Environment.getExternalStorageDirectory().canWrite();
                    Log.d(TAG, "EnvironmentCompat.getExternalStorageDirectory.canWrite returned: " + canWrite);
                }
                break;
        }
    }


    private void onRequestReadPhoneState() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_READ_PHONE_STATE);
    }

    private void onRequestWriteExternalStorage() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void onRequestIgnoringBatteryOptimizations() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }


    private void showRestartDialog() {
        Log.d(TAG, "showRestartDialog");
        RestartDialogFragment dialogFragment = new RestartDialogFragment();
        dialogFragment.show(getFragmentManager(), "RestartDialogFragment");
    }


    public static class RestartDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.d(TAG, "RestartDialogFragment#onCreateDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("書き込み不可")
                    .setMessage("外部ストレージに書き込めません\nアプリを終了しますか？")
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            System.exit(0);
                            getActivity().finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });

            return builder.create();
        }
    }
}
