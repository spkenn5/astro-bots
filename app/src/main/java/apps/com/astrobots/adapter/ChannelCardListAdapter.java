package apps.com.astrobots.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.com.astrobots.R;
import apps.com.astrobots.model.Channel;

/**
 * Created by kenji on 1/17/17.
 */

public class ChannelCardListAdapter extends RecyclerView.Adapter<ChannelCardListAdapter.ViewHolder> {

    List<String> mDataset;
    CardListListener mListener;

    public interface CardListListener{
        public void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView mChannelTitle;

        public ViewHolder(View v) {
            super(v);
            mChannelTitle = (TextView) v.findViewById(R.id.tvChannelTitle);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Channel item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChannelCardListAdapter(List<String> myDataset, CardListListener listener) {
        this.mDataset = myDataset;
        this.mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChannelCardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_channel_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mChannelTitle.setText(mDataset.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClick(position);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getItem(int position) {
        return mDataset.get(position);
    }
}
