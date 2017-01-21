package apps.com.astrobots.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

import apps.com.astrobots.R;
import apps.com.astrobots.fragment.ChannelMainFragment;
import apps.com.astrobots.fragment.TVGuideFragment;


public class AstroActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PREFS_NAME = "UsersPrefFile";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPreferences;
    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private TextView tvFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
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
                if(mPref.contains("favorite")){
                    tvFavorite.setText(String.format("Favorite : %s",mPref.getString("favorite","")));
                }else{
                    tvFavorite.setText("Favorite : N/A");
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
            Fragment cmf = new ChannelMainFragment();
            ft.replace(R.id.content_frame, cmf);
            ft.commit();
        } else if (id == R.id.nav_gallery) {
            Fragment tvGuide = new TVGuideFragment();
            ft.replace(R.id.content_frame, tvGuide);
            ft.commit();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "You selected About Astro" + id, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "You selected Contact Developer" + id, Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
