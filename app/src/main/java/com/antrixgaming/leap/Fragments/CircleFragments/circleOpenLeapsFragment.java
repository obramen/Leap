package com.antrixgaming.leap.Fragments.CircleFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.Models.ChatMessage;
import com.antrixgaming.leap.Models.UserLeap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.leaperProfileActivity;
import com.firebase.ui.auth.ui.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class circleOpenLeapsFragment extends Fragment {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbRefOpenLeap = dbRef.child("leapsforcircles").child("openleaps");
    String circleID = null;
    String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circle_open_leaps, container, false);



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        circleID = sharedPreferences.getString("currentCircleID", null);


        ListView listView = (ListView) view.findViewById(R.id.list_of_circle_open_leaps);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(getActivity(), UserLeap.class,
                R.layout.circle_open_leap_list, dbRefOpenLeap.child(circleID).orderByChild("leapSetupTime")) {
            @Override
            protected void populateView(View v, UserLeap model, int position) {

                TextView circleOpenListLeaperName = (TextView) v.findViewById(R.id.circleOpenListLeaperName);
                TextView gameType = (TextView) v.findViewById(R.id.gameType);
                TextView gameFormat = (TextView) v.findViewById(R.id.gameFormat);
                TextView gameTime = (TextView) v.findViewById(R.id.gameTime);
                TextView circleOpenListStatus = (TextView) v.findViewById(R.id.circleOpenListStatus);
                CircleImageView circleOpenListImage = (CircleImageView) v.findViewById(R.id.circleOpenListImage);
                Button circleOpenListLeapInButton = (Button) v.findViewById(R.id.circleOpenListLeapInButton);
                Button circleOpenListCancelButton = (Button) v.findViewById(R.id.circleOpenListCancelButton);

                circleOpenListLeaperName.setText(model.getleaperOne());
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(model.getleapTime());

                String leapStatus = model.getleapStatus();



                // leap button status
                // 0 - new leap
                // 1 - accepted leap
                // 2 - declined leap
                // 3 - cancelled leap

                if (Objects.equals(model.getleaperOne(), myPhoneNumber)){

                    switch (leapStatus){
                        case "0":
                            circleOpenListLeapInButton.setVisibility(v.VISIBLE);
                            circleOpenListCancelButton.setVisibility(v.VISIBLE);
                        case"1":
                            circleOpenListLeapInButton.setVisibility(v.GONE);
                            circleOpenListCancelButton.setVisibility(v.VISIBLE);

                    }
                }



                circleOpenListLeapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                circleOpenListCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                    }
                });





            }
        };

        listView.setAdapter(adapter);




        return view;
    }

}


