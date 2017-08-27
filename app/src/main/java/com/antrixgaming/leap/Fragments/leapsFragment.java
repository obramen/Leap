package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.TextView;

import com.antrixgaming.leap.R;
import com.antrixgaming.leap.newLeap;

public class leapsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaps, container, false);

        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.leap_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startNewLeapIntent = new Intent(getActivity(), newLeap.class);
                getActivity().startActivity(startNewLeapIntent);

            }
        });







        //return inflated layout
        return view;
    }

}
