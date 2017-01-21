package apps.com.astrobots.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import apps.com.astrobots.model.Program;

/**
 * Created by kenji on 1/21/17.
 */

public class ProgramAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Program> mDataset;

    public ProgramAdapter(Context mContext, List<Program> mDataset) {
        this.mContext = mContext;
        this.mDataset = mDataset;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }
}
