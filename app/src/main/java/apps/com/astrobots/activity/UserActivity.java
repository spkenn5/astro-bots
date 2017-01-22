package apps.com.astrobots.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import apps.com.astrobots.R;
import apps.com.astrobots.core.AstroConstants;
import apps.com.astrobots.core.AstroPreferences;
import apps.com.astrobots.fragment.FavoriteFragment;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences mPref;
    SharedPreferences.Editor mPreferences;

    String currentUserChannel;
    String currentUserProgram;
    boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_user);
        navigationView.setNavigationItemSelectedListener(this);

        this.mPref = getApplicationContext().getSharedPreferences(AstroPreferences.PREF_FILE, Context.MODE_PRIVATE);
        this.mPreferences = mPref.edit();

        if(mPref.contains(AstroPreferences.USER_ID)){
            this.currentUserChannel = String.format(AstroPreferences.USER_FAVORITE_CHANNEL, mPref.getString(AstroPreferences.USER_ID,""));
            this.currentUserProgram = String.format(AstroPreferences.USER_FAVORITE_PROGRAM, mPref.getString(AstroPreferences.USER_ID,""));
            loggedIn = true;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment ff = new FavoriteFragment();
        ft.replace(R.id.content_frame_user, ff);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.astro, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String url = "";

        if (id == R.id.nav_camera) {
            Intent i = new Intent(this,AstroActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_send) {
            if(loggedIn){
                String[] list = mPref.getString(AstroPreferences.ASTRO_CURRENT_USER,"").split(",");
                String userId = list[0];
                String userEmail = list[1];
                String userPhoto = list[2];
                try{
                    String favChannel = URLEncoder.encode(mPref.getString(currentUserChannel,""),"UTF-8");
                    String favProgram = URLEncoder.encode(mPref.getString(currentUserProgram,""),"UTF-8");
                    url = String.format(AstroConstants.LOCAL_API_ADD,userId,userPhoto,userEmail,favChannel,favProgram);
                    new AstroHttpRequestTask().execute(url);
                }catch(Exception e){
                    Log.e("ExceptioNCaught",e.getMessage());
                }
            }else{
                Toast.makeText(this,"You need to login",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_view) {
            if(loggedIn){
                url = String.format(AstroConstants.LOCAL_API_GET,mPref.getString(AstroPreferences.USER_ID,""));
                new AstroHttpRequestTask().execute(url);
            }else{
                Toast.makeText(this,"You need to login",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_gallery){
            if(loggedIn){
                url = String.format(AstroConstants.LOCAL_API_DELETE,mPref.getString(AstroPreferences.USER_ID,""));
                new AstroHttpRequestTask().execute(url);
                mPreferences.remove(currentUserProgram);
                mPreferences.remove(currentUserChannel);
                mPreferences.commit();
            }else{
                Toast.makeText(this,"You need to login",Toast.LENGTH_SHORT).show();
            }
        } else if(id == R.id.nav_slideshow){
            mPreferences.remove(AstroPreferences.DEFAULT_FAVORITE_CHANNEL);
            mPreferences.remove(AstroPreferences.DEFAULT_FAVORITE_PROGRAM);
            mPreferences.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class AstroHttpRequestTask extends AsyncTask<String, String, String> {

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
                Log.d("KENBUG","PostExec -> " + result);
                JSONObject resp = new JSONObject(result);
                if (Integer.valueOf(resp.get("responseCode").toString()) == 200) {
                    JSONObject arr = resp.getJSONObject("data");
                    mPreferences.putString(AstroPreferences.USER_ID,arr.getString("user_id"));
                    mPreferences.putString(AstroPreferences.ASTRO_CURRENT_USER,String.format("%s,%s,%s",arr.getString("user_id"),arr.getString("user_email"),arr.getString("user_name")));
                    mPreferences.putString(AstroPreferences.USER_FAVORITE_CHANNEL,arr.getString("user_channel_fav"));
                    mPreferences.putString(AstroPreferences.USER_FAVORITE_PROGRAM,arr.getString("user_program_fav"));
                    mPreferences.commit();
                    Toast.makeText(getApplicationContext(),"User : " + mPref.getString(AstroPreferences.USER_ID,""),Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("ExceptionCaught", e.getMessage());
            }
        }
    }

}
