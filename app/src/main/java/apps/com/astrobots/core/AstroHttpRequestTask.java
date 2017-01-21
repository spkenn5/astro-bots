package apps.com.astrobots.core;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import apps.com.astrobots.model.Channel;

/**
 * Created by kenji on 1/17/17.
 */

public class AstroHttpRequestTask extends AsyncTask<String, String, String>{

    @Override
    protected String doInBackground(String... uri) {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(uri[0]);

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();

            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}