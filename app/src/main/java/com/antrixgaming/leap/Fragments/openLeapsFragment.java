package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.antrixgaming.leap.groupInfoActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class openLeapsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_open_leaps, container, false);




        return view;

    }

}
