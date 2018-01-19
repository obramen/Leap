package com.antrixgaming.leap.LeapClasses;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.antrixgaming.leap.R;

public class ConfirmDialog {

    public Dialog dialog;
    Context context;
    String header;
    String message;
    String accept;
    String cancel;

    TextView confirmHeader;
    TextView confirmMessage;
    public TextView confirmAccept;
    public TextView confirmCancel;

    public ConfirmDialog(){
    }

    public void NewConfirmDialog(Context context, String header, String message, String accept, String cancel){

        this.context = context;
        this.header = header;
        this.message = message;
        this.accept = accept;
        this.cancel = cancel;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.dialog_confirm_action);
        dialog.setTitle(header);

        confirmHeader = (TextView) dialog.findViewById(R.id.confirmHeader);
        confirmMessage = (TextView) dialog.findViewById(R.id.confirmMessage);
        confirmAccept = (TextView) dialog.findViewById(R.id.confirmAccept);
        confirmCancel = (TextView) dialog.findViewById(R.id.confirmCancel);


        confirmHeader.setText(header);
        confirmMessage.setText(message);
        confirmCancel.setText(cancel);
        confirmAccept.setText(accept);



        this.confirmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });












        dialog.show();





    }



}
