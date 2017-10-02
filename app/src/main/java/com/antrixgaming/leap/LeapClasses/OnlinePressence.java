package com.antrixgaming.leap.LeapClasses;

import android.content.Context;

import com.antrixgaming.leap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;



public class OnlinePressence {

    String phoneNumber;
    String circleID;

    CircleImageView circleImageView;

    public OnlinePressence() {

    }

    public void circleLeaperOnlinePrescence (final Context context, final String phoneNumber, final String circleID, final CircleImageView circleImageView){

        this.circleImageView = circleImageView;
        this.phoneNumber = phoneNumber;
        this.circleID = circleID;



        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        /// SHOWING LAST ONLINE STATUS FOR CHAT
        DatabaseReference secondLeaperOnlineStatus = dbRef.child("connections").child(phoneNumber);

        secondLeaperOnlineStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("statusPermission").getValue() == null) {
                    //if unable to retrieve the value, do nothing
                }

                else {

                    final String statusPermission = dataSnapshot.child("statusPermission").getValue().toString();


                    if (Objects.equals(statusPermission, "0")) {

                        //make it grey ::: he doesn't let you see status
                        //

                        circleImageView.setBorderColor(context.getResources().getColor(R.color.grey)); /// LEAPER IS OFFLINE
                    }

                    else if (Objects.equals(statusPermission, "1")) {

                        if (Objects.equals(dataSnapshot.child("lastOnline").getValue().toString(), "true")) {


                            FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(phoneNumber).child(circleID)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override


                                        public void onDataChange(DataSnapshot dataSnapshot) {


                                            if (Objects.equals(statusPermission, "0")){

                                                circleImageView.setBorderColor(context.getResources().getColor(R.color.grey)); /// LEAPER IS OFFLINE


                                            } else if (Objects.equals(statusPermission, "1")){

                                                String yLeapStatus = dataSnapshot.child("leapStatus").getValue().toString();
                                                if (Objects.equals(yLeapStatus, "0")) {  /// DON'T ALLOW LEAPS
                                                    circleImageView.setBorderColor(context.getResources().getColor(R.color.md_red_900));
                                                } else if (Objects.equals(yLeapStatus, "1")) { // ALLOW LEAPS

                                                    circleImageView.setBorderColor(context.getResources().getColor(R.color.green)); /// LEAPER IS ONLINE

                                                }

                                            }

                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                        } else {


                            circleImageView.setBorderColor(context.getResources().getColor(R.color.grey)); /// LEAPER IS OFFLINE



                            // make it grey ::: he's offline
                        }

                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }







}
