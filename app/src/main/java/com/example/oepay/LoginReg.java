package com.example.oepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginReg extends AppCompatActivity {
    EditText password;
    Button login,register;
    private final String PREFS_NAME  = "filename";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg);

        password=findViewById(R.id.pwd);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        Context ctx = getApplicationContext();
        final SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

       // editor.putString("userId","aaa");
        //strSavedValue = sharedPreferences.getString("den", 0);

        if(sharedPreferences.getString("key",null)==null){
            login.setVisibility(View.INVISIBLE);
        }
        else{
            register.setVisibility(View.INVISIBLE);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hash=String.valueOf(password.getText().toString().hashCode());
                if(hash.equals(sharedPreferences.getString("key",null))){
                    Intent intent=new Intent(LoginReg.this,MainActivity.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(LoginReg.this,"Wrong Credentials",Toast.LENGTH_LONG).show();
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=password.getText().toString();
                editor.putString("key",String.valueOf(pass.hashCode()));
                editor.apply();
                register.setVisibility(View.INVISIBLE);


            }
        });




    }
}
