package apps.com.astrobots.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.com.astrobots.R;
//import apps.com.astrobots.adapter.ChannelPagerAdapter;
import apps.com.astrobots.adapter.FavoritePagerAdapter;

/**
 * Created by kenji on 1/22/17.
 */

public class FavoriteFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_main, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs_fav);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_fav);

        Log.d("TABLASOUT","Count: " + mTabLayout.getTabCount());
        FavoritePagerAdapter mPager = new FavoritePagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mPager);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }
}