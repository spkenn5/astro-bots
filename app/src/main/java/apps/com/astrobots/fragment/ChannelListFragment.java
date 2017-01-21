package apps.com.astrobots.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import apps.com.astrobots.MainActivity;
import apps.com.astrobots.R;
import apps.com.astrobots.adapter.CardListAdapter;
import apps.com.astrobots.model.Channel;

public class ChannelListFragment extends Fragment{
    public static final String ARG_SORT = "ARG_SORT";
    public static final String PREFS_NAME = "UsersPrefFile";


    private List<Channel> mDataset = new ArrayList<>();
    private int mSortMode;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences settings;
    private SharedPreferences.Editor mPrefs;

    public static ChannelListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_SORT, page);
        ChannelListFragment fragment = new ChannelListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortMode = getArguments().getInt(ARG_SORT);
        this.settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefs = settings.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        String url = "http://ams-api.astro.com.my/ams/v3/getChannelList";
        new AstroHttpRequestTask().execute(url);

        mAdapter = new CardListAdapter(mDataset, new CardListAdapter.CardListListener() {
            @Override
            public void onClick(int position) {
                mPrefs.putString("favorite",mDataset.get(position).getChannelTitle());
                mPrefs.commit();
                Toast.makeText(getActivity(),"Saving " + mDataset.get(position).getChannelTitle() + " as your favorite channel", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    public void checkSortingMode() {
        switch (mSortMode) {
            case 0:
                Collections.sort(mDataset, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel o1, Channel o2) {
                        return o1.getChannelStbNumber().compareTo(o2.getChannelStbNumber());
                    }
                });
                break;
            case 1:
                Collections.sort(mDataset, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel o1, Channel o2) {
                        return o1.getChannelTitle().compareTo(o2.getChannelTitle());
                    }
                });
                break;
            case 2:
                Collections.sort(mDataset, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel o1, Channel o2) {
                        return Integer.valueOf(o1.getChannelId()).compareTo(Integer.valueOf(o2.getChannelId()));
                    }
                });
                break;
            default:
                Collections.sort(mDataset, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel o1, Channel o2) {
                        return Integer.valueOf(o1.getChannelId()).compareTo(Integer.valueOf(o2.getChannelId()));
                    }
                });
                break;
        }
        mAdapter.notifyDataSetChanged();
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
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder response = new StringBuilder();
            try {
                url = new URL(uri[0]);
                Log.d("KENBUG","Url fetch");
                urlConnection = (HttpURLConnection) url
                        .openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));

                if (!url.getHost().equals(urlConnection.getURL().getHost())) {
                    urlConnection.disconnect();
                    return "";
                }
                String inputLine;
                Log.d("KENDBUG","Appending fetch");
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                Log.d("KENBUG","Finished appending fetch");
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("KENBUG","PostExec");
                List<String> channelIdList = new ArrayList<>();
                JSONObject resp = new JSONObject(result);
                if (Integer.valueOf(resp.get("responseCode").toString()) == 200) {
                    JSONArray arr = resp.getJSONArray("channels");
                    Log.d("KENBUG","Array Size : " + arr.length());
                    for (int i = 0; i < arr.length(); i++) {
                        Channel channel = new Channel(arr.getJSONObject(i).get("channelId").toString(),
                                arr.getJSONObject(i).get("channelTitle").toString(), arr.getJSONObject(i).get("channelStbNumber").toString());
                        channelIdList.add(channel.getChannelId());
                        mDataset.add(channel);
                    }
                    mPrefs.putString("channelList",channelIdList.toString().replaceAll("[\\[.\\].\\s+]", ""));
                    mPrefs.commit();
                }
                progress.dismiss();
                checkSortingMode();
            } catch (Exception e) {
                progress.dismiss();
                Log.e("ExceptionCaught", e.getMessage());
                Toast.makeText(getActivity(),"Progress takes too long, closing app", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
