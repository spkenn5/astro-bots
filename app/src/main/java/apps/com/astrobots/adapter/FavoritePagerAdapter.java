package apps.com.astrobots.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import apps.com.astrobots.fragment.FavoriteListFragment;

/**
 * Created by kenji on 1/21/17.
 */

public class FavoritePagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Channel", "Program"};

    public FavoritePagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new FavoriteListFragment().newInstance(position);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}