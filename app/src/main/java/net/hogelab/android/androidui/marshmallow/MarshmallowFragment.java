package net.hogelab.android.androidui.marshmallow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
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


    private View mButtonContainer;


    public MarshmallowFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marshmallow, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TextView textView;
            Button button;
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
        } else {
            TextView textView = (TextView) getActivity().findViewById(R.id.textViewIsMarshmallowText);
            textView.setText("No");

            textView = (TextView) getActivity().findViewById(R.id.textViewCanReadPhoneStateText);
            textView.setText("N/A");

            textView = (TextView) getActivity().findViewById(R.id.textViewCanWriteExternalStorageText);
            textView.setText("N/A");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if (isGranted) {
                    Toast.makeText(getActivity(), "READ_PHONE_STATE is granted", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (isGranted) {
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL_STORAGE is granted", Toast.LENGTH_LONG).show();
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
}
