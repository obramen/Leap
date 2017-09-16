package com.antrixgaming.leap.Fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antrixgaming.leap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfileFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String nickname = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        TextView fullName = (TextView) view.findViewById(R.id.nickname);
        fullName.setText(nickname);




        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.profileContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Tab layout with tabs
        tabLayout = (TabLayout) view.findViewById(R.id.profileTabs);
        tabLayout.setupWithViewPager(mViewPager);


        CircleImageView profileImage = (CircleImageView) view.findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Change profile picture", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();            }
        });

        return view;
    }




    // Pager adapter initialize and import of fragments
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new recentLeapsFragment();
                case 1:
                    return new leapHistoryFragment();
                case 2:
                    return new openLeapsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Leaps Live";
                case 1:
                    return "My History";
                case 2:
                    return "Open Leaps";
                default:
                    return null;

            }
        }
    }

}
