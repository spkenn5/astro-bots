package apps.com.astrobots.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.com.astrobots.R;
import apps.com.astrobots.adapter.ChannelCardListAdapter;
import apps.com.astrobots.core.AstroPreferences;

/**
 * Created by kenji on 1/22/17.
 */

public class FavoriteListFragment extends Fragment {
    public static final String ARG_SORT = "ARG_SORT";

    private List<String> mDataset = new ArrayList<>();
    private String userId;
    private int mSortMode;
    private boolean loginStatus = false;
    private String currentUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SharedPreferences settings;
    private SharedPreferences.Editor mPrefs;


    public static FavoriteListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_SORT, page);
        FavoriteListFragment fragment = new FavoriteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortMode = getArguments().getInt(ARG_SORT);
        Log.d("PAGE","PAGE : " + mSortMode);
        this.settings = getActivity().getApplicationContext().getSharedPreferences(AstroPreferences.PREF_FILE, Context.MODE_PRIVATE);
        this.mPrefs = settings.edit();
        this.loginStatus = settings.getBoolean(AstroPreferences.LOGIN_STATUS,false);
        if(loginStatus){
            this.userId = settings.getString(AstroPreferences.USER_ID,"");
            switch (mSortMode){
                case 0:
                    currentUser = String.format(AstroPreferences.USER_FAVORITE_CHANNEL,userId);
                    break;
                case 1:
                    currentUser = String.format(AstroPreferences.USER_FAVORITE_PROGRAM,userId);
                    break;
            }
            mDataset = new ArrayList<>();
        }else{
            switch (mSortMode){
                case 0:
                    currentUser = AstroPreferences.DEFAULT_FAVORITE_CHANNEL;
                    break;
                case 1:
                    currentUser = AstroPreferences.DEFAULT_FAVORITE_PROGRAM;
                    break;
            }
            mDataset = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_list_favorite, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view_fav);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        new AstroHttpRequestTask().execute(currentUser);

        mAdapter = new ChannelCardListAdapter(mDataset, new ChannelCardListAdapter.CardListListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity(),"Saving " + mDataset.get(position) + " as your favorite channel", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    class AstroHttpRequestTask extends AsyncTask<String, String, String> {
        ProgressDialog progress = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            progress.setTitle("Populating data from server");
            progress.setMessage("Please wait for the process to be completed.");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... uri) {
            Log.d("KENBUG","list -> " + uri[0]);
            String[] channelTitle = settings.getString(uri[0],"").split(",");
            Log.d("KENBUG","list -> " + channelTitle.toString());
            for(String sstr : channelTitle){
                if(sstr.length() > 0){
                    Log.d("KENBUG","Appending " + sstr);
                    mDataset.add(sstr);
                }
            }
            return settings.getString(uri[0],"");
        }

        @Override
        protected void onPostExecute(String s) {
            Collections.sort(mDataset);
            mAdapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }
}
