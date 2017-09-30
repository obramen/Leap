package com.antrixgaming.leap;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;


import com.antrixgaming.leap.Fragments.NewLeapFragments.newLeapFragment;
import com.antrixgaming.leap.Fragments.NewLeapFragments.newRankedLeapFragment;
import com.antrixgaming.leap.Fragments.NewLeapFragments.newWagerLeapFragment;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class newLeap extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_leap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.newLeapsContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.newLeapTabs);
        tabLayout.setupWithViewPager(mViewPager);

    }



        public interface KeyEventListener extends View.OnKeyListener {
            boolean isVisible();
            View getView();
        }

        private final List<KeyEventListener> keyEventHandlerList = new ArrayList<>();

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            for (KeyEventListener handler : keyEventHandlerList) {
                if (handleKeyEvent(handler, event)) {
                    return true;
                }
            }
            return super.dispatchKeyEvent(event);
        }

        public void addKeyEventHandler(@NonNull KeyEventListener handler) {
            keyEventHandlerList.add(handler);
        }

        public void removeKeyEventHandler(@NonNull KeyEventListener handler) {
            keyEventHandlerList.remove(handler);
        }

        /**
         * @return <tt>true</tt> if the event was handled, <tt>false</tt> otherwise
         */
        private boolean handleKeyEvent(@Nullable KeyEventListener listener, KeyEvent event) {
            return listener != null
                    && listener.isVisible()
                    && listener.onKey(listener.getView(), event.getKeyCode(), event);
        }










        /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new newLeapFragment();
                case 1:
                    return new newWagerLeapFragment();
                case 2:
                    return new newRankedLeapFragment();
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.FreeLeap);
                case 1:
                    return getString(R.string.WageredLeap);
                case 2:
                    return getString(R.string.RankedLeap);
                default:
                return null;

            }
        }
    }


    //back when return arrow is clicked
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
