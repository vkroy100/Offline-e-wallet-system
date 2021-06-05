package com.example.oepay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ConfirmationScreen extends AppCompatActivity {

    private String amt;
    private TextView confirmationMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_screen);

        confirmationMsg = findViewById(R.id.msg);



        Intent intent = getIntent();
        boolean recvd = intent.getBooleanExtra("recvd", false);
        String amt = intent.getStringExtra("amt");

        if(recvd){
            String finalMsg = "Received Rs. " + amt + " successfully.";
            confirmationMsg.setText(finalMsg);
        } else{
            String finalMsg = "Paid Rs. " + amt + " successfully.";
            confirmationMsg.setText(finalMsg);
        }



    }
}
