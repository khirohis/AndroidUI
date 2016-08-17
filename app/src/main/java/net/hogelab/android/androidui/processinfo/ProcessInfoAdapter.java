package net.hogelab.android.androidui.processinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hogelab.android.androidui.R;

import java.util.List;

/**
 * Created by kobayasi on 2016/08/16.
 */
public class ProcessInfoAdapter
        extends RecyclerView.Adapter<ProcessInfoAdapter.ViewHolder> {
    private final String TAG = ProcessInfoAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private List<ActivityManager.RunningAppProcessInfo> mData;

    public ProcessInfoAdapter(Context context, List<?> data) {
        mInflater = LayoutInflater.from(context);
        mData = (List<ActivityManager.RunningAppProcessInfo>) data;
    }

    @Override
    public ProcessInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.list_item_processinfo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        ActivityManager.RunningAppProcessInfo info = null;
        if (mData != null && mData.size() > i) {
            info = mData.get(i);
        }

        if (info != null) {
            viewHolder.textName.setText(info.processName);
            viewHolder.textImportance.setText(Integer.toString(info.importance));
        } else {
            viewHolder.textName.setText(null);
            viewHolder.textImportance.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textImportance;

        public ViewHolder(View itemView) {
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.text_processinfo_name);
            textImportance = (TextView) itemView.findViewById(R.id.text_processinfo_importance);
        }
    }
}
