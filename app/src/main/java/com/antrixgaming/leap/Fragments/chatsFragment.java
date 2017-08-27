package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.antrixgaming.leap.Leap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.newLeap;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

public class chatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_chat, container, false);

        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.chat_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





            }
        });









        //return inflated layout
        return view;


    }



}



