package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.antrixgaming.leap.Leap;
import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_chat;
import com.antrixgaming.leap.newLeap;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class chatsFragment extends Fragment {

    // Firebase instance variables

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_chat, container, false);

        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.chat_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent openOneChat = new Intent(getActivity(), activity_one_chat.class);
                startActivity(openOneChat);



            }
        });
















        //return inflated layout
        return view;


    }


}







