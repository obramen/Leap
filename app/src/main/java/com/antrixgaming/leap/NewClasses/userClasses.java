package com.antrixgaming.leap.NewClasses;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



public class userClasses {
}


class Chat {
    private String mName;
    private String mMessage;
    private String mUid;



    public Chat() {
        // Needed for Firebase
    }

    public Chat(String name, String uid, String message) {
        mName = name;
        mMessage = message;
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }
}

class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mMessageField;

    public ChatHolder(View itemView) {
        super(itemView);
        mNameField = (TextView) itemView.findViewById(android.R.id.text1);
        mMessageField = (TextView) itemView.findViewById(android.R.id.text2);
    }

    public void setName(String name) {
        mNameField.setText(name);
    }

    public void setMessage(String message) {
        mMessageField.setText(message);
    }
}
