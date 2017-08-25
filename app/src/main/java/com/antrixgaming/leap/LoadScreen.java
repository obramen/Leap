package com.antrixgaming.leap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LoadScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);
        getSupportActionBar().hide();


        final TextView registerText = (TextView) findViewById(R.id.registerText);

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(LoadScreen.this, registerLogin.class);
                LoadScreen.this.startActivity(registerIntent);

                Toast.makeText(LoadScreen.this, "Registration Successful", Toast.LENGTH_LONG).show();


            }
        });







    }
}
