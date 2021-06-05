package com.example.oepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RechargeScreen extends AppCompatActivity {

    private EditText userId,amount,password;
    private Button recharge;
    private final String PREFS_NAME  = "filename";
    private final String KEY_DENSITY    = "den";
    private int strSavedValue;
    static boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flag=false;
        userId=findViewById(R.id.userId);
        amount=findViewById(R.id.amount);
        password=findViewById(R.id.password);
        recharge=findViewById(R.id.recharge);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amou = amount.getText().toString(),userid=userId.getText().toString();
                String pass = password.getText().toString();
                if(!TextUtils.isEmpty(amou) || !TextUtils.isEmpty(userid) || !TextUtils.isEmpty(pass)){

                    Context ctx   = getApplicationContext();
                    SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    strSavedValue = sharedPreferences.getInt("den", 0);
                    editor.putInt(KEY_DENSITY, Integer.parseInt(amou)+strSavedValue);
                    editor.apply();
                    Intent intent=new Intent(RechargeScreen.this,MainActivity.class);
                    startActivity(intent);

                }
                else{

                    Toast.makeText(RechargeScreen.this,"Enter all the details",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(RechargeScreen.this,MainActivity.class);
                startActivity(intent);
                // app icon in action bar clicked; go home
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
         Intent intent=new Intent(RechargeScreen.this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(RechargeScreen.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

}
