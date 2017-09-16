package com.antrixgaming.leap;

import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.Fragments.MatchFragments.game1Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game2Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game3Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game4Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game5Fragment;
import com.antrixgaming.leap.LeapClasses.UserLeap;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class leapDetailsActivity extends AppCompatActivity {


    TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.

        Bundle bundle = getIntent().getExtras();
        String leapID = bundle.getString("leapID");

        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("leaps").child(myUID).child(leapID);


        ListView listOfDetails = (ListView)findViewById(R.id.list_of_Leap_details);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(this, UserLeap.class,
                R.layout.activity_leap_details_listview,
                dbRef) {


            @Override
            protected void populateView(View v, final UserLeap model, int position) {

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.detailsMatchContainer);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                //Tab layout with tabs
                tabLayout = (TabLayout) findViewById(R.id.detailsMatchTabs);
                tabLayout.setupWithViewPager(mViewPager);







                // Get references to the views of message.xml
                //final TextView leapID = (TextView)v.findViewById(R.id.leapID);
                TextView gameType = (TextView)v.findViewById(R.id.detailsGameType);
                TextView gameFormat = (TextView)v.findViewById(R.id.detailsGameFormat);
                final TextView countdownTimer = (TextView)v.findViewById(R.id.detailsCountdownTimer);
                TextView leaperOne = (TextView)v.findViewById(R.id.detailsLeaperOne);
                TextView leaperTwo = (TextView)v.findViewById(R.id.detailsLeaperTwo);
                TextView gameTime = (TextView)v.findViewById(R.id.detailsLeapTime);
                //final Button leapInButton = (Button) v.findViewById(R.id.leapInButton);
                //final Button recordScoreButton = (Button) v.findViewById(R.id.recordScoreButton);
                //final CardView leapCard = (CardView) v.findViewById(R.id.leapCard);

                final String lDay = model.getleapDay();
                final String lTime = model.getleapTime();

                // Set their texts
                String leapID = model.getleapID();
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(lDay + ", " +lTime);
                leaperOne.setText(model.getleaperOne());
                leaperTwo.setText(model.getleaperTwo());

                int leapStatus = Integer.parseInt(model.getleapStatus());

                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        countdownTimer.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        countdownTimer.setText("LIVE!");
                        //countdownTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
                        countdownTimer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }.start();




            }
        };

        listOfDetails.setAdapter(adapter);












    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
