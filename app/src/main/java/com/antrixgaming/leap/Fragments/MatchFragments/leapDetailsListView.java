package com.antrixgaming.leap.Fragments.MatchFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.antrixgaming.leap.R;

public class leapDetailsListView extends AppCompatActivity{



    TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap_details_listview);



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.detailsMatchContainer);




        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Tab layout with tabs
        tabLayout = (TabLayout) findViewById(R.id.detailsMatchTabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {




        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new game1Fragment();
                case 1:
                    return new game2Fragment();
                case 2:
                    return new game3Fragment();
                case 3:
                    return new game4Fragment();
                case 4:
                    return new game5Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Game 1";
                case 1:
                    return "Game 2";
                case 2:
                    return "Game 3";
                case 3:
                    return "Game 4";
                case 4:
                    return "Game 5";
                default:
                    return null;

            }
        }
    }








}
