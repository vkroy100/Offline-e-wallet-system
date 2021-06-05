package com.example.oepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {
    private Button pay;
//    private int REQUEST_ENABLE_BLUETOOTH=1;
//    private BluetoothAdapter bluetoothAdapter;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private TextView balance;
    private final String PREFS_NAME  = "filename";
    static boolean flag=false;
//    private final String KEY_DENSITY    = "den";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dl = findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        balance=findViewById(R.id.balance);
        Context ctx = getApplicationContext();
        int strSavedValue = 0;
        flag=false;
        copy("myproject.bks");
        String device = "B";
        copy("user"+device+"_keystore.bks");
        copy("user"+device+"_privkey.pem");
        //flag=true;
//        Date currentTime = Calendar.getInstance().getTime();
//        Toast.makeText(MainActivity.this,currentTime.toString(),Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       // int strSavedValue = sharedPreferences.getInt("den", 0);
//        if(sharedPreferences.getString("userId",null)==null) {
//
//            editor.putString("userId", );
//            editor.apply();
//        }
        editor.putString("userId","aaa");
        strSavedValue = sharedPreferences.getInt("den", 0);
        balance.setText(String.valueOf("Balance: "+strSavedValue));
        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.nav_home:
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_transaction_history:
//                        Toast.makeText(MainActivity.this,"fff",Toast.LENGTH_SHORT).show();
                        Intent intent11=new Intent(MainActivity.this,TransactionHistory.class);
                        startActivity(intent11);
                        break;

                    case R.id.nav_recharge:
                        Intent intent1=new Intent(MainActivity.this,RechargeScreen.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(MainActivity.this, "My Tools",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        pay=findViewById(R.id.pay);
//        receive=findViewById(R.id.receive);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,NearBySend.class);
                intent.putExtra("key","0");
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onPause() {
//
//        Intent intent=new Intent(MainActivity.this,LoginReg.class);
//        startActivity(intent);
//        super.onPause();
//    }

//        @Override
//    protected void on() {
//        super.onResume();
//        Intent intent=new Intent(MainActivity.this,LoginReg.class);
//        startActivity(intent);
//
//    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(MainActivity.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

    public void copy(String keystore){
        try {
            InputStream inputStream = getApplicationContext().getAssets().open(keystore);
            String outPath = getApplicationContext().getFilesDir().getPath().toString() + "/" + keystore;
            OutputStream outputStream = new FileOutputStream(outPath);

            byte[] buffer = new byte[1024];
            int length;
            while((length = inputStream.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e){
            System.out.println("Error while copying jks:" + e.toString());
            e.printStackTrace();
        }
    }
    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
////        if(!flag) {
////            Intent intent = new Intent(MainActivity.this, LoginReg.class);
////            startActivity(intent);
////        }
//    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        Intent intent=new Intent(MainActivity.this,LoginReg.class);
//        startActivity(intent);
//    }


}
