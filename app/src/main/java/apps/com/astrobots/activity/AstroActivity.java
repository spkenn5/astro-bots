package apps.com.astrobots.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import apps.com.astrobots.R;
import apps.com.astrobots.core.AstroPreferences;
import apps.com.astrobots.fragment.ChannelListFragment;
import apps.com.astrobots.fragment.SettingsFragment;
import apps.com.astrobots.fragment.TVGuideFragment;


public class AstroActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPreferences;
    private ViewPager mPager;

    GoogleApiClient mGoogleApiClient;

    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private TextView tvFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setContentView(R.layout.activity_astro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mPref = getApplicationContext().getSharedPreferences(AstroPreferences.PREF_FILE,Context.MODE_PRIVATE);
        this.mPreferences = mPref.edit();

        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_astro);
        tvFavorite = (TextView) headerLayout.findViewById(R.id.textView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(mPref.contains(AstroPreferences.USER_ID)){
                    tvFavorite.setText(String.format("USER ID : %s",mPref.getString(AstroPreferences.USER_ID,"")));
                }else{
                    tvFavorite.setText("User ID : N/A");
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment cmf = new ChannelListFragment();
        ft.replace(R.id.content_frame, cmf);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment fragSettings = new SettingsFragment();
            ft.replace(R.id.content_frame, fragSettings);
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (id == R.id.nav_camera) {
            Fragment cmf = new ChannelListFragment();
            ft.replace(R.id.content_frame, cmf);
            ft.commit();
        } else if (id == R.id.nav_gallery) {
            Fragment tvGuide = new TVGuideFragment();
            ft.replace(R.id.content_frame, tvGuide);
            ft.commit();
        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(this,UserActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection failed",Toast.LENGTH_SHORT).show();
    }

    public GoogleApiClient getClient (){
        return mGoogleApiClient;
    }
}
