package com.example.oepay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.SecretKey;

public class DummyPayActivity extends AppCompatActivity {

    InputStream inputStream;
    OutputStream outputStream;
    final static int MESSAGE_READ = 1;
    NearBySend.SendReceive sendReceive;

    NearBySend.ServerClass serverClass;
    NearBySend.ClientClass clientClass;
    String type;
    SecretKey sharedSessionKey;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummypay);
        type = Helper.getType();
        sendReceive = Helper.getSendReceive();
        clientClass = Helper.getClientClass();
        serverClass = Helper.getServerClass();
        sharedSessionKey = Helper.getSharedSessionKey();
        Toast.makeText(getApplicationContext(), type+":"+(serverClass==null)+","+(clientClass==null)+","+(sendReceive==null)+","+(sharedSessionKey==null), Toast.LENGTH_SHORT).show();
        new SendMessage("Hello there!").execute();


    }



    public class SendMessage extends AsyncTask<Void, Void, Void> {
        String msg;

        public SendMessage(String msg){
            this.msg = msg;
        }

        @Override
        protected void onPreExecute() {
            System.out.println("Started SendMessage: "+msg);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(sendReceive != null) {
                try {
                    sendReceive.write(msg.getBytes());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }


    }

    /*private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        int bytes;
        byte[] buffer;
        int msgNo = 0;

        public SendReceive(Socket skt){
            socket = skt;
            buffer = new byte[1024];
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                outputStream.write("Hello!".getBytes());
                System.out.println("Thread started.");

            }catch(Exception e){
                e.printStackTrace();
            }


        }
        @Override
        public void run(){

            while(socket!=null){
                if(interrupted()){
                    return;
                }
                try {
                    System.out.println("Inside thread...");
                    System.out.println(inputStream.available());
                    bytes = inputStream.read(buffer);
                    if(bytes>0){
                        System.out.println("recvd msg: "+new String(buffer));
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        System.out.println("Reached here...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}
