package com.example.oepay;

import android.accounts.AuthenticatorException;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NearBySend extends AppCompatActivity {
    private ListView sendDevice;
//    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice[] btArray;
    static boolean flag=false;

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    IntentFilter intentFilter;

    WifiBroadcastReceiver broadcastReceiver;

    WifiP2pDevice[] deviceArray;

    X509Certificate myCert, cacert;

    PrivateKey myPrivatekey;

    SecretKey sharedSecretKey;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;
    final static int MESSAGE_READ = 1, AUTH_SUCCESS = 2;

    KeyStore keyStore;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_send);
        sendDevice=findViewById(R.id.sendDevices);


        String device = "B";


        try {

            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            KeyStore myKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] keyStorePassword = "test123".toCharArray();
            try(InputStream keyStoreData = new FileInputStream(getApplicationContext().getFilesDir().getPath().toString() + "/myproject.bks")){
                keyStore.load(keyStoreData, keyStorePassword);
            }

            try(InputStream keyStoreData = new FileInputStream(getApplicationContext().getFilesDir().getPath().toString() + "/user"+device+"_keystore.bks")){
                myKeyStore.load(keyStoreData,keyStorePassword);
            }

            cacert = (X509Certificate) keyStore.getCertificate("CA_cert");
            myCert = (X509Certificate) myKeyStore.getCertificate("user"+device+"_cert");
            System.out.println(cacert);
            System.out.println("my certificate: ");
            System.out.println(myCert);
            myCert.verify(cacert.getPublicKey());
            myPrivatekey = readPrivKey("user"+device+"_privkey.pem");
            System.out.println("My private key: ");
            System.out.println(myPrivatekey);

        } catch(Exception e){
            System.out.println("Threw exception: "+e.toString());
            e.printStackTrace();
        }






        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

        channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        broadcastReceiver = new WifiBroadcastReceiver();
        broadcastReceiver.setNearByDevicesHandler(this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);


        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Discovery started successfully...", Toast.LENGTH_SHORT).show();
                System.out.println("Discovery started successfully...");
//                        status.setText("Discovery started successfully...");
//                Intent myIntent = new Intent(getApplicationContext(), NearByDevices.class);
//                myIntent.putExtra("device", new WifiP2pDevice());
//                startActivity(myIntent);

            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Discovery failed to start!");
//                status.setText("Discovery failed to start!");
            }
        });


        sendDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice curr = deviceArray[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = curr.deviceAddress;

                wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + curr.deviceName, Toast.LENGTH_SHORT).show();
                        // connect.setEnabled(false);
                        //disconnect.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Failed connecting to " + curr.deviceName, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//        flag=false;
//        implementListeners();
//
    }
    /*private void implementListeners(){
//        Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
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
            sendDevice.setAdapter(arrayAdapter);
        }
        sendDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(NearBySend.this,PayScreen.class);
                intent.putExtra("name",btArray[position]);
                startActivity(intent);
            }
        });
    }*/





    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            System.out.println("Started peerListListener...");
            if (!peerList.getDeviceList().isEmpty()) {
                System.out.println("Device list is not empty!");
                Collection<WifiP2pDevice> peers = peerList.getDeviceList();
                deviceArray = new WifiP2pDevice[peers.size()];
                int index = 0;
                String[] devices = new String[peers.size()];
                for (WifiP2pDevice device : peers) {
                    System.out.println(device.deviceName + '\t' + device.deviceAddress);
                    devices[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, devices);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sendDevice.setAdapter(adapter);
            }
        }
    };


    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress inetAddress = info.groupOwnerAddress;

            if (info.groupFormed && info.isGroupOwner){
                System.out.println("Host");
                type = "Host";
                Helper.setType(type);
//                status.setText("Host");
                serverClass = new ServerClass();
                serverClass.start();
                Helper.setServerClass(serverClass);

            } else if (info.groupFormed){
                System.out.println("Client");
                type = "Client";
                Helper.setType(type);
                //status.setText("Client");

                clientClass = new ClientClass(inetAddress);
                clientClass.start();
                Helper.setClientClass(clientClass);
            }
        }
    };


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = Arrays.copyOf((byte[]) msg.obj,msg.arg1);
                    byte[] tempMsg = null;
                    try {
                        tempMsg = decryptAES(readBuff, sharedSecretKey);
                        System.out.println("RECVD: "+new String(tempMsg));

                        byte code = tempMsg[0];
                        byte[] actualMsg = new byte[tempMsg.length-1];
                        System.arraycopy(tempMsg, 1, actualMsg, 0, actualMsg.length);

                        switch (code){
                            case 33: System.out.println("Received amount: "+new String(actualMsg));

                        }




                        Toast.makeText(getApplicationContext(), new String(tempMsg), Toast.LENGTH_SHORT).show();

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //System.out.println("Sending msg: "+tempMsg);
                    /*if(tempMsg.equals("SIGKILL")){
                        clientClass.interrupt();
                        clientClass = null;
                        sendReceive.interrupt();
                        sendReceive = null;
                        serverClass = null;
                    }*/
                    if(tempMsg!=null) {
                        ;
                    }
                    break;
                case AUTH_SUCCESS:
                    Toast.makeText(getApplicationContext(), "Mutual authentication successfull!",Toast.LENGTH_SHORT).show();

            }
            return true;
        }
    });




    public class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        int bytes;
        byte[] buffer;
        private final String PREFS_NAME  = "filename";
        private SQLiteDatabaseHandler dbHandler;
        SharedPreferences prefs;
        private String othCN;
        int msgNo = 0;
        Context ctx = NearBySend.this;

        public SendReceive(Socket skt){
            socket = skt;
            buffer = new byte[1024];
            prefs = NearBySend.this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            dbHandler = new SQLiteDatabaseHandler(NearBySend.this);
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                System.out.println("Authentication starts!");

                if(authenticate()) {
                    System.out.println("Mutual authentication successfull!");
                    handler.obtainMessage(AUTH_SUCCESS, 0, -1, "".getBytes()).sendToTarget();
                    Helper.setSendReceive(this);
                    Intent intent = new Intent(NearBySend.this, PayScreen.class);
                    startActivity(intent);
                } else{
                    System.out.println("Mutual authentication failed!");
                    throw new AuthenticatorException();
                }



            }catch(Exception e){
                e.printStackTrace();
            }


        }

        private void sendOwnCertificate(X509Certificate cert) throws Exception{
            byte[] certBytes = cert.getEncoded();
            outputStream.write(certBytes);
        }

        private X509Certificate receiveOthersCertificate() throws Exception{
            byte[] recCertBytes = new byte[2048];
            X509Certificate othersCertificate;
            int size = inputStream.read(recCertBytes);
            if(size>0){
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                InputStream temp = new ByteArrayInputStream(recCertBytes);
                othersCertificate = (X509Certificate) certificateFactory.generateCertificate(temp);
                temp.close();
                return othersCertificate;
            }
            return null;
        }

        private boolean authenticate() throws Exception{
            sendOwnCertificate(myCert);
            X509Certificate othersCert = receiveOthersCertificate();
            PublicKey othersPubKey;
            System.out.println("Certificate status: ");
            try{
                System.out.println("Others certificate: ");
                System.out.println(othersCert);
                othersCert.verify(cacert.getPublicKey());
                othersPubKey = othersCert.getPublicKey();
                othCN = othersCert.getSubjectX500Principal().getName();
                othCN = othCN.substring(othCN.indexOf("=")+1, othCN.indexOf(","));

                Helper.setUserId(othCN);
            } catch(Exception e){
                e.printStackTrace();
                System.out.println("Certificate invalid!");
                return false;
            }
            System.out.println("Certificate is valid!");


            System.out.println("myPubKey");
            System.out.println(myCert.getPublicKey());

            System.out.println("othPubKey");
            System.out.println(othersPubKey);

            SecureRandom rand = new SecureRandom();

            byte[] challengeToBeSent = new byte[16], myPartOfKey = new byte[16];

            byte[] challengeReceived = new byte[16], othPartOfKey = new byte[16];

            rand.nextBytes(challengeToBeSent);

            rand.nextBytes(myPartOfKey);

            byte[] chalConcatKeyToBeSent = new byte[32];

            System.arraycopy(challengeToBeSent, 0, chalConcatKeyToBeSent, 0, 16);
            System.arraycopy(myPartOfKey, 0, chalConcatKeyToBeSent, 16, 16);
            System.out.println("ChallengeToBeSent:");
            for(int i=0;i<16;i++){
                System.out.println(challengeToBeSent[i]);
            }

            System.out.println("MyPartOfKey:");
            for(int i=0;i<16;i++){
                System.out.println(myPartOfKey[i]);
            }

            System.out.println("encryptedRSAchallengeSent");
            byte[] e = encrypt(chalConcatKeyToBeSent, othersPubKey);
            System.out.println("Size after encryption: "+e.length);
            outputStream.write(e);

            byte[] er = new byte[e.length];
            int size2 = inputStream.read(er);



            byte[] chalConcatKeyReceived;

            if(size2>0){

                chalConcatKeyReceived = decrypt(er, myPrivatekey);

                System.out.println("challengeReceived:");
                System.arraycopy(chalConcatKeyReceived, 0, challengeReceived, 0, 16);
                System.arraycopy(chalConcatKeyReceived, 16, othPartOfKey, 0, 16);
                for(int i=0;i<16;i++){
                    System.out.println(challengeReceived[i]);
                }

                System.out.println("OthPartOfKey:");
                for(int i=0;i<16;i++){
                    System.out.println(othPartOfKey[i]);
                }

                System.arraycopy(chalConcatKeyReceived, 0, challengeReceived, 0, 16);
                System.arraycopy(chalConcatKeyReceived, 16, othPartOfKey, 0, 16);

//
                byte[] sharedKey = new byte[16];

                for(int i=0;i<16;i++){
                    sharedKey[i] = (byte)(myPartOfKey[i]^othPartOfKey[i]);
                    System.out.println("myPart: " + (int)myPartOfKey[i] + ", othPart: " + (int)othPartOfKey[i] + ", sharedKey: " + (int)sharedKey[i]);
                }

                sharedSecretKey = new SecretKeySpec(sharedKey, 0, sharedKey.length, "AES");

                byte[] responseToBeSent = encryptAES(challengeReceived, sharedSecretKey);

                System.out.println("Sizeof AES encryption: "+challengeReceived.length+" : "+responseToBeSent.length);

                for(int i=0;i<responseToBeSent.length;i++){
                    System.out.println(responseToBeSent[i]);
                }
                outputStream.write(responseToBeSent);

                byte[] buffer = new byte[responseToBeSent.length];
                int size4 = inputStream.read(buffer);
                if(size4>0){
//                    byte[] responseReceived = Arrays.copyOf(buffer,responseToBeSent.length);
                    byte[] responseToVerify = decryptAES(buffer, sharedSecretKey);
                    System.out.println("Response received");
                    for(int i = 0;i<responseToVerify.length;i++){
                        System.out.println(responseToVerify[i]);
                        if(responseToVerify[i] != challengeToBeSent[i]){
                            System.out.println("Different response!");
                            return false;
                        }
                    }
                    Helper.setSharedSessionKey(sharedSecretKey);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void run(){

            while(socket!=null){
                if(isInterrupted()){
                    try {
                        socket.close();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    bytes = inputStream.read(buffer);

                    if(bytes>0){
                        byte[] readBuff = Arrays.copyOf(buffer, bytes);
                        byte[] tempMsg = null;
                            tempMsg = decryptAES(readBuff, sharedSecretKey);
                            System.out.println("RECVD: "+new String(tempMsg));

                            byte code = tempMsg[0];
                            byte[] actualMsg = new byte[tempMsg.length-1];
                            System.arraycopy(tempMsg, 1, actualMsg, 0, actualMsg.length);

                            switch (code){
                                case 33: String recvdAmt = new String(actualMsg);
                                         System.out.println("Received amount: "+recvdAmt);
                                         String myUserId = myCert.getSubjectX500Principal().getName();
                                         myUserId = myUserId.substring(myUserId.indexOf("=")+1, myUserId.indexOf(","));
                                         TransactionRecord tr = new TransactionRecord(othCN, myUserId, recvdAmt, Calendar.getInstance().getTime().toString());
                                         Transaction transaction = new Transaction();
                                         transaction.setRecptUserId(myUserId);
                                         String hash = String.valueOf(tr.hashCode());
                                         transaction.setId(Integer.parseInt(hash));
                                         transaction.setSenderUserId(othCN);
                                         transaction.setDate(tr.getDate());
                                         transaction.setAmount(Integer.parseInt(recvdAmt));
                                         SignedTransaction signedTransaction = new SignedTransaction(transaction);
                                         signedTransaction.signTransactionRecord(myPrivatekey);
//                                         byte[] encoded = Base64.encode(signedTransaction.signature, Base64.DEFAULT);
                                         System.out.println(new String(signedTransaction.signature));

                                         byte[] signedTransactionByte;
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        ObjectOutput out = null;
                                        try{
                                            out = new ObjectOutputStream(baos);
                                            out.writeObject(signedTransaction);
                                            out.flush();

                                            signedTransactionByte = baos.toByteArray();
                                        } finally {
                                            try {
                                                baos.close();
                                            } catch(IOException ex){
                                                ;
                                            }
                                        }
                                        byte[] finalSignedTransactionByte = new byte[signedTransactionByte.length+1];
                                        finalSignedTransactionByte[0] = 12;
                                        System.arraycopy(signedTransactionByte, 0, finalSignedTransactionByte, 1, signedTransactionByte.length);
                                        write(finalSignedTransactionByte);
                                         int strSavedValue = prefs.getInt("den",0);
                                         prefs.edit().putInt("den",strSavedValue+Integer.parseInt(recvdAmt)).apply();
                                         dbHandler.addtransaction(signedTransaction);
                                         Intent intent = new Intent(ctx, ConfirmationScreen.class);
                                         intent.putExtra("amt",recvdAmt);
                                         intent.putExtra("recvd",true);
                                         startActivity(intent);
                                         break;

                                case 12: String recvdHash = new String(actualMsg);
                                        ByteArrayInputStream bais = new ByteArrayInputStream(actualMsg);
                                        ObjectInputStream ois = new ObjectInputStream(bais);
                                        SignedTransaction signedTransaction1 = (SignedTransaction)ois.readObject();
                                        int currBal = prefs.getInt("den", 0);
                                        prefs.edit().putInt("den",currBal-Integer.parseInt(signedTransaction1.getAmount())).apply();
                                        dbHandler.addtransaction(signedTransaction1);

                                         System.out.println("Received transaction record");
                                         System.out.println(new String(signedTransaction1.signature));

                                         Intent intent1 = new Intent(ctx, ConfirmationScreen.class);
                                         intent1.putExtra("amt", signedTransaction1.getAmount());
                                         intent1.putExtra("recvd", false);
                                         startActivity(intent1);
                                         break;



                            }
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        public void write(byte[] bytes){
            try {

                outputStream.write(encryptAES(bytes, sharedSecretKey));
                Intent intent = new Intent(NearBySend.this, NearByReceive.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress host){
            hostAdd = host.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            if(isInterrupted()){
                try{
                    socket.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
                return;
            }
            try {
                socket.connect(new InetSocketAddress(hostAdd,19999),500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return null;
        }
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run(){
            if(isInterrupted()){
                try{
                    serverSocket.close();
                    socket.close();
                } catch(Exception e){
                    e.printStackTrace();
                }

                return;
            }
            try {
                serverSocket = new ServerSocket(19999);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return null;
        }
    }

    public byte[] encrypt(byte[] msg, PublicKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(msg);
    }

    public byte[] decrypt(byte[] ci, PrivateKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ci);
    }


    public PrivateKey readPrivKey(String filename) throws Exception{
        String keyPem = "";
        BufferedReader br = new BufferedReader(new FileReader(getApplicationContext().getFilesDir().getPath().toString() + "/" + filename));
        String line;
        while((line = br.readLine()) != null){
            keyPem += line + "\n";
        }
        br.close();
        keyPem = keyPem.replace("-----BEGIN PRIVATE KEY-----\n", "");
        keyPem = keyPem.replace("-----END PRIVATE KEY-----","");
        byte[] encoded = Base64.decode(keyPem,Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return kf.generatePrivate(keySpec);
    }

    public byte[] encryptAES(byte[] msg, SecretKey secretKey) throws Exception{
        String initVector = "encryptionIntVec";
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        //        return Base64.encodeToString(encrypted,Base64.DEFAULT);
        return cipher.doFinal(msg);
    }

    public byte[] decryptAES(byte[] encrypted, SecretKey secretKey) throws Exception{
        String initVector = "encryptionIntVec";
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);


        return cipher.doFinal(encrypted);
    }
//    private class ServerClass extends Thread
//    {
//        private BluetoothServerSocket serverSocket;
//
//        public ServerClass(){
//            try {
//                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run()
//        {
//            BluetoothSocket socket=null;
//
//            while (socket==null)
//            {
//                try {
//                    Message message=Message.obtain();
//                    message.what=STATE_LISTENING;
//                    handler.sendMessage(message);
//
//                    socket=serverSocket.accept();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Message message=Message.obtain();
//                    message.what=STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message);
//                }
//
//                if(socket!=null)
//                {
//                    Message message=Message.obtain();
//                    message.what=STATE_CONNECTED;
//                    handler.sendMessage(message);
//
//                    sendReceive=new SendReceive(socket);
//                    sendReceive.start();
//
//                    break;
//                }
//            }
//        }
//    }
//    Handler handler=new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//
//            switch (msg.what)
//            {
//                case STATE_LISTENING:
//                    Toast.makeText(NearBySend.this,"Listening",Toast.LENGTH_LONG).show();
////                    status.setText("Listening");
//                    break;
//                case STATE_CONNECTING:
//                    Toast.makeText(NearBySend.this,"Connecting",Toast.LENGTH_LONG).show();
////                    status.setText("Connecting");
//                    break;
//                case STATE_CONNECTED:
//                    Toast.makeText(NearBySend.this,"Connected",Toast.LENGTH_LONG).show();
////                    status.setText("Connected");
//                    break;
//                case STATE_CONNECTION_FAILED:
//                    Toast.makeText(NearBySend.this,"Connection Failed",Toast.LENGTH_LONG).show();
////                    status.setText("Connection Failed");
//                    break;
//                case STATE_MESSAGE_RECEIVED:
//                    byte[] readBuff= (byte[]) msg.obj;
//                    String tempMsg=new String(readBuff,0,msg.arg1);
//                    Toast.makeText(NearBySend.this,"Receive " + tempMsg , Toast.LENGTH_LONG).show();
//                    String msgg="Received Successfully";
//                    sendReceive.write(msgg.getBytes());
////                    msg_box.setText(tempMsg);
//                    break;
//            }
//            return true;
//        }
//    });
//
//
//    private class ClientClass extends Thread
//    {
//        private BluetoothDevice device;
//        private BluetoothSocket socket;
//
//        public ClientClass (BluetoothDevice device1)
//        {
//            device=device1;
//
//            try {
//                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run()
//        {
//            try {
//                socket.connect();
//                Message message=Message.obtain();
//                message.what=STATE_CONNECTED;
//                handler.sendMessage(message);
//
//                sendReceive=new SendReceive(socket);
//                sendReceive.start();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Message message=Message.obtain();
//                message.what=STATE_CONNECTION_FAILED;
//                handler.sendMessage(message);
//            }
//        }
//    }
//    private class SendReceive extends Thread
//    {
//        private final BluetoothSocket bluetoothSocket;
//        private final InputStream inputStream;
//        private final OutputStream outputStream;
//
//        public SendReceive (BluetoothSocket socket)
//        {
//            bluetoothSocket=socket;
//            InputStream tempIn=null;
//            OutputStream tempOut=null;
//
//            try {
//                tempIn=bluetoothSocket.getInputStream();
//                tempOut=bluetoothSocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            inputStream=tempIn;
//            outputStream=tempOut;
//        }
//
//        public void run()
//        {
//            byte[] buffer=new byte[1024];
//            int bytes;
//
//            while (true)
//            {
//                try {
//                    bytes=inputStream.read(buffer);
//                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void write(byte[] bytes)
//        {
//            try {
//                outputStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(NearBySend.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

}
