package apps.com.astrobots.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.TimeZone;

import apps.com.astrobots.R;
import apps.com.astrobots.adapter.ProgramAdapter;
import apps.com.astrobots.model.Channel;
import apps.com.astrobots.model.Program;

/**
 * Created by kenji on 1/21/17.
 */

public class TVGuideFragment extends Fragment {

    public static final String PREFS_NAME = "UsersPrefFile";
    SharedPreferences mPrefs;
    SharedPreferences.Editor mPrefsEditor;
    ProgramAdapter mAdapter;

    List<Program> mDataset = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv_guide, container, false);

        GridView gvProgramList = (GridView) view.findViewById(R.id.gridview);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.add(Calendar.HOUR,-1);
        String startDate = sdf.format(cal.getTime());
        cal.add(Calendar.HOUR,+3);
        String endDate = sdf.format(cal.getTime());
        String url = "http://ams-api.astro.com.my/ams/v3/getEvents?channelId=%s&periodStart=%s&periodEnd=%s";
        String pass = "";
        if(mPrefs.contains("channelList")){
            pass = mPrefs.getString("channelList","");
            try{
                startDate = URLEncoder.encode(startDate,"UTF-8");
                endDate = URLEncoder.encode(endDate,"UTF-8");
                url = (String.format(url,pass,startDate,endDate));
            }catch(Exception e){
                Log.e("KENBUG",e.getMessage());
            }
            Log.d("KENBUG", pass);
        }else{
            Log.d("KENBUG","Not found");
        }
        new AstroHttpRequestTask().execute(url);

        mAdapter = new ProgramAdapter(getActivity(), mDataset);
        gvProgramList.setAdapter(mAdapter);

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
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder response = new StringBuilder();
            try {
                url = new URL(uri[0]);
                Log.d("KENBUG","Url fetch");
                urlConnection = (HttpURLConnection) url
                        .openConnection();
                Log.d("KENBUG","Response Code: " + urlConnection.getResponseCode());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));

                if (!url.getHost().equals(urlConnection.getURL().getHost())) {
                    urlConnection.disconnect();
                    return "";
                }
                String inputLine;
                Log.d("KENBUG","Appending fetch");
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                Log.d("KENBUG","Finished appending fetch -> " + response.toString());
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
                JSONObject resp = new JSONObject(result);
                if (Integer.valueOf(resp.get("responseCode").toString()) == 200) {
                    JSONArray arr = resp.getJSONArray("getevent");
                    Log.d("KENBUG","Size : " + arr.length());
                    for (int i = 0; i < arr.length(); i++) {
                        Program program = new Program();
                        program.setChannelId(arr.getJSONObject(i).getString("channelId"));
                        program.setChannelStbNumber(arr.getJSONObject(i).getString("channelStbNumber"));
                        program.setChannelTitle(arr.getJSONObject(i).getString("channelTitle"));
                        program.setDisplayTime(arr.getJSONObject(i).getString("displayDateTime"));
                        program.setProgramTitle(arr.getJSONObject(i).getString("programmeTitle"));
                        mDataset.add(program);
                    }
                }
                progress.dismiss();
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d("KENBUG", e.getMessage());
                progress.dismiss();
                Toast.makeText(getActivity(),"Progress takes too long...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
