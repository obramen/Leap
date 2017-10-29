package com.antrixgaming.leap;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.GameList;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class activity_circle_settings extends BaseActivity {

    DatabaseReference dbRef;
    DatabaseReference settingsRef;
    Context context;

    Bundle bundle;
    String circleID;

    TextView selectGame;
    TextView defaultGame;
    CheckBox lockDefaultGameCheckBox;
    CheckBox publicCheckBox;
    Dialog gameDialog;
    private TextView gameTitle;

    StorageReference mStorage;
    LeapUtilities leapUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_circle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");


        context = activity_circle_settings.this;
        dbRef = FirebaseDatabase.getInstance().getReference();

        bundle = getIntent().getExtras();
        circleID = bundle.getString("circleID");

        settingsRef = dbRef.child("groupcircles").child(circleID);

        gameDialog = new Dialog(context);

        mStorage = FirebaseStorage.getInstance().getReference();
        leapUtilities = new LeapUtilities();



        selectGame = (TextView)findViewById(R.id.selectGame);
        defaultGame = (TextView)findViewById(R.id.defaultGame);
        lockDefaultGameCheckBox = (CheckBox)findViewById(R.id.lockDefaultGameCheckBox);
        publicCheckBox = (CheckBox)findViewById(R.id.publicCheckBox);


        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.child("publicGroup").getValue() == null){

                }else {
                    String publicGroup = dataSnapshot.child("publicGroup").getValue().toString();

                    if (Objects.equals(publicGroup, "true")){
                        publicCheckBox.setChecked(true);
                    } else {
                        publicCheckBox.setChecked(false);
                    }
                }









                if (dataSnapshot.child("defaultGame").getValue() == null) {

                } else {
                    String mDefaultGame = dataSnapshot.child("defaultGame").getValue().toString();

                    defaultGame.setText(mDefaultGame);

                }




                if (dataSnapshot.child("lockDefaultGame").getValue() == null){

                } else {
                    String lockDefaultGame = dataSnapshot.child("lockDefaultGame").getValue().toString();

                    if (Objects.equals(lockDefaultGame, "true")){
                        lockDefaultGameCheckBox.setChecked(true);
                    } else {
                        lockDefaultGameCheckBox.setChecked(false);
                    }

                }













            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        publicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (publicCheckBox.isChecked()){

                    if (Objects.equals(defaultGame.getText().toString(), "")){
                        Toast.makeText(context, "Public groups require a default game. Select default game.", Toast.LENGTH_LONG).show();
                        publicCheckBox.setChecked(false);

                    } else{
                        settingsRef.child("publicGroup").setValue("true");
                    }
                } else{
                    settingsRef.child("publicGroup").setValue("false");
                }
            }
        });




        lockDefaultGameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lockDefaultGameCheckBox.isChecked()){
                    settingsRef.child("lockDefaultGame").setValue("true");
                } else{
                    settingsRef.child("lockDefaultGame").setValue("false");
                }
            }
        });








        selectGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gameDialog.setContentView(R.layout.dialog_game_list);

                final ListView listView = (ListView)gameDialog.findViewById(R.id.listOfGames);

                final FirebaseListAdapter<GameList> adapter;
                adapter = new FirebaseListAdapter<GameList>(activity_circle_settings.this, GameList.class, R.layout.game_list, dbRef.child("gamelist")) {
                    @Override
                    protected void populateView(View v, final GameList model, int position) {

                        ImageView gameListImage = (ImageView)v.findViewById(R.id.gameListImage);
                        gameTitle = (TextView)v.findViewById(R.id.gameListGameTitle);
                        TextView gameListId = (TextView)v.findViewById(R.id.gameListId);

                        gameTitle.setText(model.getGameTitle());

                        String gameid = model.getGameid();
                        gameListId.setText(gameid);

                        StorageReference mGameImageStorage = mStorage.child("gameImages").child(gameid  + ".jpg");
                        leapUtilities.SquareImageFromFirebase(context, mGameImageStorage, gameListImage);






                    }
                };

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gameTitle = (TextView)view.findViewById(R.id.gameListGameTitle);

                        settingsRef.child("defaultGame").setValue(gameTitle.getText().toString());
                        Toast.makeText(context, "Default game changed to " + gameTitle.getText().toString(), Toast.LENGTH_LONG).show();

                        gameDialog.dismiss();


                    }
                });

                gameDialog.show();



            }
        });



















    }




    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
