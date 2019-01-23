package net.hogelab.android.androidui.chooser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.hogelab.android.androidui.R;
import net.hogelab.pfw.PFWFragment;

import java.util.ArrayList;
import java.util.List;

public class IntentChooserFragment extends PFWFragment {
    private final String TAG = IntentChooserFragment.class.getSimpleName();


    private boolean mResumed;

    private EditText mEditSendText;
    private EditText mEditSendUrl;
    private Button mSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intent_chooser, container, false);

        mEditSendText = rootView.findViewById(R.id.edit_send_text);
        mEditSendText.setText("♪青空のラプソディ/fhána #NowPlaying");

        mEditSendUrl = rootView.findViewById(R.id.edit_send_url);
        mEditSendUrl.setText("http://fhana.jp");

        mSend = rootView.findViewById(R.id.button_send);
        mSend.setOnClickListener(
                (view) -> onSend(mEditSendText.getText().toString(), mEditSendUrl.getText().toString()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        mResumed = false;
    }


    private void onSend(String text, String url) {
        snsShare2(getActivity(), text, url);
    }


    private static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";

    private boolean snsShare2(@NonNull Activity activity, @NonNull String text, @NonNull String url) {
        ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(text + " " + url)
                .startChooser();

        return true;
    }

    private boolean snsShare(@NonNull Activity activity, @NonNull String text, @NonNull String url) {
        String textAndUrl = text + " " + url;

        Intent baseIntent = new Intent(Intent.ACTION_SEND);
        baseIntent.setType("text/plane");
        baseIntent.putExtra(Intent.EXTRA_SUBJECT, text);
        baseIntent.putExtra(Intent.EXTRA_TEXT, textAndUrl);

        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PackageManager.MATCH_ALL;
        } else {
            flags = PackageManager.MATCH_DEFAULT_ONLY;
        }
        List<ResolveInfo> resolveInfo = activity.getPackageManager().queryIntentActivities(baseIntent, flags);
        if (resolveInfo == null || resolveInfo.isEmpty()) {
            return false;
        }

        List<Intent> intents = new ArrayList<>();
        for (ResolveInfo info : resolveInfo) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plane");

            String packageName = info.activityInfo.packageName;
            if (packageName.equalsIgnoreCase(FACEBOOK_PACKAGE_NAME)) {
                // Facebook は text を受け付けない
                intent.putExtra(Intent.EXTRA_TEXT, url);
            } else if (packageName.equalsIgnoreCase(TWITTER_PACKAGE_NAME)) {
                // Twitter は url 混じりの text を受け付ける
                intent.putExtra(Intent.EXTRA_TEXT, textAndUrl);
            } else {
                // その他は安全のため text のみとする
                intent.putExtra(Intent.EXTRA_TEXT, text);
            }

            intent.setClassName(packageName, info.activityInfo.name);
            intents.add(intent);
        }

        Intent representativeIntent = intents.remove(0);
        Parcelable[] remainingIntent = intents.toArray(new Parcelable[0]);
        Intent chooserIntent = Intent.createChooser(representativeIntent, "シェア");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, remainingIntent);
        startActivity(chooserIntent);

        return true;
    }
}
