package com.example.oepay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

public class NearByReceive extends AppCompatActivity {
    private ListView receiveDevice;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice[] btArray;
    private boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_receive);
        flag=false;
        receiveDevice=findViewById(R.id.receiveDevices);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        implementListeners();
    }
    private void implementListeners(){
        Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        btArray=new BluetoothDevice[bt.size()];
        int index=0;
        if( bt.size()>0)
        {
            for(BluetoothDevice device : bt)
            {
                btArray[index]= device;
                strings[index]=device.getName();
                index++;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
            receiveDevice.setAdapter(arrayAdapter);
        }
        receiveDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                NearBySend.ClientClass clientClass = new NearBySend.ClientClass(btArray[position]);
//                clientClass.start();
                Intent intent=new Intent(NearByReceive.this,ReceiveScreen.class);
                intent.putExtra("name",btArray[position]);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(NearByReceive.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

}
