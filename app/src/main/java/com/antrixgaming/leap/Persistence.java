package com.antrixgaming.leap;


import com.google.firebase.database.FirebaseDatabase;

public class Persistence{


    public static FirebaseDatabase getDatabase(){
        if(FirebaseDatabase.getInstance()==null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        return FirebaseDatabase.getInstance();
    }
}
