package com.yahoo.americancurry.petpeeve.adapters;

/**
 * Created by rakeshch on 2/25/15.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.yahoo.americancurry.petpeeve.activities.PinnedListFragment;


public class PinListPagerAdapter extends SmartFragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String[] tabTitles = {"Pinned", "Pin Received"};
    private Context context;
    public PinListPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new PinnedListFragment();
        } else if (position == 1) {
            return new PinnedListFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    /**
     * Created by rakeshch on 2/18/15.
     */
    public abstract static class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        // Sparse array to keep track of registered fragments in memory
        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Register the fragment when the item is instantiated
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        // Unregister when the item is inactive
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        // Returns the fragment for the position (if instantiated)
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}

