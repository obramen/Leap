package com.antrixgaming.leap.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.DatePickerFragment;
import com.antrixgaming.leap.LeapClasses.UserLeap;
import com.antrixgaming.leap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;


public class newLeapFragment extends Fragment {

    private EditText timeTextView;
    private EditText dateTextView;
    private Button leapButton;
    private TextView leaperOne;
    private TextView leaperTwo;
    private String myUID;
    private TextView gameType;
    private Spinner gameFormat;



    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_leap, container, false);

        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        timeTextView = (EditText) view.findViewById(R.id.freeLeapTime);
        dateTextView = (EditText) view.findViewById(R.id.freeLeapDate);
        leapButton = (Button) view.findViewById(R.id.freeLeapConfirmButton);
        leaperOne = (TextView) view.findViewById(R.id.leaperOne);
        leaperTwo = (TextView) view.findViewById(R.id.leaperTwo);
        gameType = (TextView) view.findViewById(R.id.freeLeapGameType);
        gameFormat = (Spinner) view.findViewById(R.id.freeLeapGameFormat);

        leaperOne.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        //final String mGameType = gameType.getText().toString();
        //final String mGameFormat = gameFormat.getSelectedItem().toString();
        //final String mLeaperOne = leaperOne.getText().toString();
        //final String mLeaperTwo = leaperTwo.getText().toString();
        //final String mLeapDate = dateTextView.getText().toString();
        //final String mLeapTime = timeTextView.getText().toString();

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //DialogFragment newFragment = new TimePickerFragment();
                //newFragment.show(getFragmentManager(), "timePicker");

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);


                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {



                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                timeTextView.setText(hourOfDay + ":" + minute);
                            }
                }, mHour, mMinute, false);

                timePickerDialog.show();






            }




        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), "datePicker");


                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);




                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {



                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);



                                // Get Current Time
                                final Calendar c = Calendar.getInstance();
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);


                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                        new TimePickerDialog.OnTimeSetListener() {



                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                                timeTextView.setText(hourOfDay + ":" + minute);
                                            }
                                        }, mHour, mMinute, false);

                                if (dayOfMonth == new Date().getTime()){


                                }

                                timePickerDialog.show();





                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());

                datePickerDialog.show();

            }


        });

        leapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mGameType = gameType.getText().toString();
                String mGameFormat = gameFormat.getSelectedItem().toString();
                String mLeaperOne = leaperOne.getText().toString();
                String mLeaperTwo = leaperTwo.getText().toString();
                String mLeapDate = dateTextView.getText().toString();
                String mLeapTime = timeTextView.getText().toString();

                // status "0" show a new leap. "1" = accepted. "2" = declined. "3" = cancelled

                String key = FirebaseDatabase.getInstance().getReference().child("leaps").child(myUID).push().getKey();
                FirebaseDatabase.getInstance().getReference().child("leaps").child(myUID)
                        .child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                        mLeaperTwo, mLeapDate, mLeapTime, "0"));

                Toast.makeText(getActivity(), "you just leaped", Toast.LENGTH_LONG).show();

                getActivity().finish();



            }
        });







        return view;


    }

}



