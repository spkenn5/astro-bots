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

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    List<Channel> mDataset;
    CardListListener mListener;

    public interface CardListListener{
        public void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView mChannelId, mChannelTitle, mChannelSbt;

        public ViewHolder(View v) {
            super(v);
            mChannelId = (TextView) v.findViewById(R.id.tvChannelId);
            mChannelTitle = (TextView) v.findViewById(R.id.tvChannelTitle);
            mChannelSbt = (TextView) v.findViewById(R.id.tvChannelSbt);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void add(int position, Channel item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Channel item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardListAdapter(List<Channel> myDataset, CardListListener listener) {
        this.mDataset = myDataset;
        this.mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel name = mDataset.get(position);
        holder.mChannelId.setText(mDataset.get(position).getChannelId());
        holder.mChannelTitle.setText(mDataset.get(position).getChannelTitle());
        holder.mChannelSbt.setText(mDataset.get(position).getChannelStbNumber());
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

    public Channel getItem(int position) {
        return mDataset.get(position);
    }
}
