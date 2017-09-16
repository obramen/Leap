package com.antrixgaming.leap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;



public class registerLogin extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    public boolean mVerificationInProgress = false;
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        getSupportActionBar().hide();


        //assign progress bar
        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]





        if(mAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            spinner.setVisibility(View.GONE);

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast

            //Verify Login Token
            Intent openLeapIntent = new Intent(registerLogin.this, Leap.class);
            registerLogin.this.startActivity(openLeapIntent);
            Toast.makeText(registerLogin.this, "This works, already signed in", Toast.LENGTH_LONG).show();

            finish();



        }





        //assign Leap button
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);


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
                Toast.makeText(registerLogin.this, "Verification Successful", Toast.LENGTH_LONG).show();
                signInWithPhoneAuthCredential(credential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                Toast.makeText(registerLogin.this, "Account Problem, Contact Admin", Toast.LENGTH_LONG).show();


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(registerLogin.this, "Invalid phone number provided", Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(registerLogin.this, "SMS quota for project has been exceeded", Toast.LENGTH_LONG).show();
                }

                spinner.setVisibility(View.GONE);


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
                Toast.makeText(registerLogin.this, "Sent: Check for verification code", Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                //assign country code picker view from layout
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                //assign phoneNumber view from layout
                EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);


                //assign phone number to variable CarrierNumberEditText inside CountryCodePicker class.
                // More info here https://github.com/hbb20/CountryCodePickerProject
                ccp.registerCarrierNumberEditText(phoneNumber);


                // get phone number with country code and plus
                String phoneNumberAndCode = ccp.getFullNumber().toString();





                //Open Phone Verification screen (phoneVerifyActivity) to enter CODE
                Intent openPhoneVerifyIntent = new Intent(registerLogin.this, phoneVerifyActivity.class);
                //send verification ID and phone number used via intent
                openPhoneVerifyIntent.putExtra("mVerificationId", mVerificationId);
                openPhoneVerifyIntent.putExtra("phoneNumber", phoneNumberAndCode);

                //countryCode is sent to the phoneVerifyActivity activity which is sent to Leap activity if verification is successful.
                openPhoneVerifyIntent.putExtra("countryCode", ccp.getSelectedCountryCode());

                registerLogin.this.startActivity(openPhoneVerifyIntent);
                spinner.setVisibility(View.GONE);

            }







        };





        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                //assign country code picker from layout
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                //assign phoneNumber from layout
                EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);

                if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    //mVerificationField.setError("Cannot be empty.");
                    phoneNumber.setHint("Cannot be empty");
                    phoneNumber.setHintTextColor(getResources().getColor(R.color.cherry));


                    return;
                }



                spinner.setVisibility(View.VISIBLE);

                //remove "0" from number if any
                int firstDigit = Integer.parseInt((phoneNumber.getText().toString()).substring(0, 1));

                if (firstDigit < 1){
                    phoneNumber.setText(phoneNumber.getText().toString().substring(1));
                }

                //assign phone number to variable CarrierNumberEditText inside CountryCodePicker class.
                // More info here https://github.com/hbb20/CountryCodePickerProject
                ccp.registerCarrierNumberEditText(phoneNumber);


                // get phone number with country code and plus
                String phoneNumberAndCode = ccp.getFullNumber().toString().trim();
                Toast.makeText(registerLogin.this, "The Full Number is " + phoneNumberAndCode, Toast.LENGTH_LONG).show();


                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumberAndCode,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        registerLogin.this,  // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks


            }



        });

    }



    // Open Main Screen on successful login
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(registerLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                            //Open main interface for Leap while passing usr value
                            Intent openLeapIntent = new Intent(registerLogin.this, Leap.class);
                            openLeapIntent.putExtra("countryCode", ccp.getSelectedCountryCode());
                            openLeapIntent.putExtra("countryCodeStatus", "1");


                            registerLogin.this.startActivity(openLeapIntent);
                            finish();



                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(registerLogin.this, "Unexpected error, retry login", Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(registerLogin.this, "Invalid credential recorded", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });

        spinner.setVisibility(View.GONE);


    }


    public void onTokenRefresh() {

        //Check user's credentials
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            //String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            // ...


                            CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                            //Open main interface for Leap while passing usr value
                            //code status is used to tell the Leap activity where the extras is coming from.
                            // the number "0" is used to signal that the intent is coming from splash screen activity.
                            // Hence it comes with country code attached to it
                            Intent openLeapIntent = new Intent(registerLogin.this, Leap.class);
                            openLeapIntent.putExtra("countryCode", ccp.getSelectedCountryCode());
                            openLeapIntent.putExtra("countryCodeStatus", "1");
                            registerLogin.this.startActivity(openLeapIntent);
                            finish();

                        } else {
                            // Handle error -> task.getException();

                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(registerLogin.this, "This doesn't work, re-login", Toast.LENGTH_LONG).show();



                        }
                    }

                });


    }





}


