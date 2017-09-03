package com.antrixgaming.leap;

import android.content.Intent;
import android.os.Handler;
import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phoneVerifyActivity extends AppCompatActivity {


    //TODO fix phoneVery.java / phone verification scree crash after first login


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ProgressBar spinner;

    // 15min progress bar declaration
    private int progress = 0;
    private final int pBarMax = 600;


    public String countryCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);


        Bundle bundle = getIntent().getExtras();
        final String mVerificationID = bundle.getString("mVerificationId");
        final String phoneNumber = bundle.getString("phoneNumber");
        final String countryCode = bundle.getString("countryCode");



        //15 minutes progress bar
        final ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar2);
        pBar.setMax(pBarMax);
        final Thread pBarThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(progress<=pBarMax) {
                        pBar.setProgress(progress);
                        sleep(1000);
                        ++progress;
                    }
                }
                catch(InterruptedException e) {
                }
            }
        };
        pBarThread.start();





        final EditText verificationCode = (EditText) findViewById(R.id.verificationCode);
        final Button btnVerify = (Button) findViewById(R.id.btnSendV);
        final Button btnResendVerify = (Button) findViewById(R.id.btnResendV);
        final TextView vPhoneNumber = (TextView) findViewById(R.id.vPhoneNumber);
        vPhoneNumber.setText(phoneNumber);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]



            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    //Log.d(TAG, "onVerificationCompleted:" + credential);
                    Toast.makeText(phoneVerifyActivity.this, "Verification Successful", Toast.LENGTH_LONG).show();
                    signInWithPhoneAuthCredential(credential);

                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    //Log.w(TAG, "onVerificationFailed", e);

                    Toast.makeText(phoneVerifyActivity.this, "Account Problem, Contact Admin", Toast.LENGTH_LONG).show();


                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                        Toast.makeText(phoneVerifyActivity.this, "Invalid phone number provided", Toast.LENGTH_LONG).show();

                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                        Toast.makeText(phoneVerifyActivity.this, "SMS quota for project has been exceeded", Toast.LENGTH_LONG).show();
                    }

                    // Show a message and update the UI
                    // ...

                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    //Log.d(TAG, "onCodeSent:" + verificationId);
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(phoneVerifyActivity.this, "Code Resent", Toast.LENGTH_LONG).show();

                    // Save verification ID and resending token so we can use them later
                    String mVerificationId = verificationId;
                    verificationCode.setHint("- - - - - -");


                }






            };



            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String verificationId = mVerificationID;
                    // [START verify_with_code]
                    if (TextUtils.isEmpty(verificationCode.getText().toString())) {
                        //mVerificationField.setError("Cannot be empty.");
                        verificationCode.setHint("Enter Code");
                        verificationCode.setHintTextColor(getResources().getColor(R.color.cherry));

                        return;
                    }



                    spinner.setVisibility(View.VISIBLE);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode.getText().toString().trim());
                    signInWithPhoneAuthCredential(credential);
                    // [END verify_with_code]
                    }

            });


            btnResendVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    spinner.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            phoneVerifyActivity.this,  // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks


                }
            });

        }









        // Open Main Screen on successful login
        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(phoneVerifyActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = task.getResult().getUser();



                                Bundle bundle = getIntent().getExtras();
                                final String countryCode = bundle.getString("countryCode");


                                //Open main interface for Leap while passing usr value
                                Intent openLeapIntent = new Intent(phoneVerifyActivity.this, Leap.class);

                                //Open main interface for Leap while passing usr value
                                //code status is used to tell the Leap activity where the extras is coming from.
                                // the number "1" is used to signal that the intent is coming from phoneVerify activity or registerLogin activity.
                                // Hence it comes with country code attached to it
                                openLeapIntent.putExtra("countryCode", countryCode);
                                openLeapIntent.putExtra("countryCodeStatus", "1");


                                phoneVerifyActivity.this.startActivity(openLeapIntent);
                                finish();



                            } else {
                                // Sign in failed, display a message and update the UI
                                //Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(phoneVerifyActivity.this, "Unexpected error, retry login", Toast.LENGTH_LONG).show();

                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    Toast.makeText(phoneVerifyActivity.this, "Invalid credential recorded", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    });




        }

        boolean doubleBackToExitPressedOnce = false;

        @Override
        public void onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press twice to cancel verification", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }


}
