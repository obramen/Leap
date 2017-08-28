package com.antrixgaming.leap;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class activity_one_chat extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_chat);

        //Get UID of user


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView messages = (RecyclerView) findViewById(R.id.messages);
        //messages.setLayoutManager(new LinearLayoutManager(this));




        FloatingActionButton sendFab =
                (FloatingActionButton)findViewById(R.id.send_chat_fab);

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child("messages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),"THIS CIRCLE",
                                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), FirebaseAuth.getInstance().getCurrentUser().getUid())
                        );

                // Clear the input
                input.setText("");
            }
        });



        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);


        FirebaseListAdapter<ChatMessage> adapter;
            adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                    R.layout.messages, FirebaseDatabase.getInstance().getReference().child("messages")) {

                @Override
                protected void populateView(View v, ChatMessage model, int position) {
                    // Get references to the views of message.xml
                    TextView messageText = (TextView)v.findViewById(R.id.message_text);
                    //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                    TextView phoneNumber = (TextView)v.findViewById(R.id.phoneNumber);
                    TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    phoneNumber.setText(model.getPhoneNumber());
                    //messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("HH:mm",
                            model.getMessageTime()));





                }
            };

            listOfMessages.setAdapter(adapter);






    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one_chat, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_one_chat_profile) {
            return true;
        }
        else if (id == R.id.action_one_chat_history) {
            return true;
        }

        return super.onOptionsItemSelected(item);




    }






    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}

