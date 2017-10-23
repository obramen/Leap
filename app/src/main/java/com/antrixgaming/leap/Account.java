package com.antrixgaming.leap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hubtel.payments.Class.Environment;
import com.hubtel.payments.Class.PaymentItem;
import com.hubtel.payments.Exception.HubtelPaymentException;
import com.hubtel.payments.HubtelCheckout;
import com.hubtel.payments.Interfaces.OnPaymentResponse;
import com.hubtel.payments.SessionConfiguration;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });







        try {
            SessionConfiguration sessionConfiguration = new SessionConfiguration()
                    .Builder().setClientId("CLIENT-ID")
                    .setSecretKey("SECRET-KEY")
                    .setEnvironment(Environment.LIVE_MODE)
                    .build();
            HubtelCheckout hubtelPayments = new HubtelCheckout(sessionConfiguration);
            hubtelPayments.setPaymentDetails(1.5, "Wallet Top-up");
            hubtelPayments.Pay(this);
            hubtelPayments.setOnPaymentCallback(new OnPaymentResponse() {
                @Override
                public void onFailed(String token, String reason) {
                }

                @Override
                public void onCancelled() {
                }

                @Override
                public void onSuccessful(String token) {
                }
            });
        }
        catch (HubtelPaymentException e) {
            e.printStackTrace();
        }





    }

}
