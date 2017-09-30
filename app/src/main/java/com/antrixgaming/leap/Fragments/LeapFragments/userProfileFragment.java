package com.antrixgaming.leap.Fragments.LeapFragments;


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

import com.antrixgaming.leap.Fragments.leapHistoryFragment;
import com.antrixgaming.leap.Fragments.openLeapsFragment;
import com.antrixgaming.leap.Fragments.recentLeapsFragment;
import com.antrixgaming.leap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfileFragment extends Fragment {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String nickname = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);





        return view;
    }




}
