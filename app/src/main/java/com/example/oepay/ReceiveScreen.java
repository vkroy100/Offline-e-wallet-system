package com.example.oepay;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.crypto.SecretKey;

public class ReceiveScreen extends AppCompatActivity {
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static final String APP_NAME = "OEPay";
//    public SendReceive sendReceive;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private final String PREFS_NAME  = "filename";
    private final String KEY_DENSITY    = "den";
    private ProgressDialog progressDialog;
    private TextView textView;
    private SQLiteDatabaseHandler sqLiteDatabaseHandler;
    private String othersuserId,amount;
    static boolean flag=false;


    private KeyStore keyStore;
    private X509Certificate cacert,myCert;
    private PrivateKey myPrivatekey;


    private SecretKey sharedSecretKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_screen);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        textView=findViewById(R.id.msg);
        flag=false;
        progressDialog= new ProgressDialog(this);
        sqLiteDatabaseHandler=new SQLiteDatabaseHandler(this);
        progressDialog.show();
//        final ServerClass serverClass=new ServerClass();
//        serverClass.start();

        Bundle bundle = getIntent().getExtras();
        bluetoothDevice = (BluetoothDevice) bundle.get("name");
//        ClientClass clientClass = new ClientClass(bluetoothDevice);
//        clientClass.start();
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendReceive.write(payMoney.getText().toString().getBytes());
//            }
//        });
//        String device = "B";
        String device = "A";

//        copy("myproject.bks");
//        copy("user"+device+"_keystore.bks");
//        copy("user"+device+"_privkey.pem");


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
//            myPrivatekey = readPrivKey("user"+device+"_privkey.pem");
            System.out.println("My private key: ");
            System.out.println(myPrivatekey);

        } catch(Exception e){
            System.out.println("Threw exception: "+e.toString());
            e.printStackTrace();
        }



    }
    /*private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    Toast.makeText(ReceiveScreen.this,"Listening",Toast.LENGTH_LONG).show();
                    break;
                case STATE_CONNECTING:
                    Toast.makeText(ReceiveScreen.this,"Connecting",Toast.LENGTH_LONG).show();
                    break;
                case STATE_CONNECTED:
                    Toast.makeText(ReceiveScreen.this,"Connected",Toast.LENGTH_LONG).show();
                    break;
                case STATE_CONNECTION_FAILED:
                    Toast.makeText(ReceiveScreen.this,"Connection Failed",Toast.LENGTH_LONG).show();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readCode=new byte[3];
                    byte[] readBuff= (byte[]) msg.obj;
                    //Toast.makeText(ReceiveScreen.this,String.valueOf(msg.arg1),Toast.LENGTH_SHORT).show();
                    readCode[0] = readBuff[0];
                    readCode[1] = readBuff[1];
                    readCode[2] = readBuff[2];
                    byte[] msggg=new byte[msg.arg1-3];
                    for(int i=0;i<msggg.length;i++){
                        msggg[i]=readBuff[i+3];
                    }

                    //
                    if(readCode[0] == 0 && readCode[1] == 0 && readCode[2] == 0){
                        String tempMsg=new String(msggg,0,msg.arg1-3);
                        othersuserId=tempMsg;
//                        Toast.makeText(ReceiveScreen.this,tempMsg,Toast.LENGTH_SHORT).show();
                    }

                    if(readCode[0]==0 && readCode[1]==0 && readCode[2]==1){
                        String tempMsg=new String(msggg,0,msg.arg1-3);
                        amount=tempMsg;
                        progressDialog.dismiss();
//                        String msgg="Received Successfully by user";
                        Context ctx   = getApplicationContext();
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        int strSavedValue = sharedPreferences.getInt("den", 0);
                        editor.putInt(KEY_DENSITY, strSavedValue+Integer.parseInt(tempMsg));
                        editor.apply();
                        Date currentTime = Calendar.getInstance().getTime();
                        TransactionRecord transactionRecord=new TransactionRecord(othersuserId,amount,currentTime.toString());
                        int hashcde = transactionRecord.hashCode();
                        Transaction transaction=new Transaction(hashcde,currentTime.toString(),othersuserId,Integer.parseInt(amount));
                        sqLiteDatabaseHandler.addtransaction(transaction);
                        //Toast.makeText(ReceiveScreen.this,String.valueOf(hashcde),Toast.LENGTH_LONG).show();
                        byte[] msgs=String.valueOf(hashcde).getBytes();
                        byte[] total=new byte[msgs.length+3];
                        total[0]=0;
                        total[1]=0;
                        total[2]=1;
                        for(int i=3;i<total.length;i++){
                            total[i]=msgs[i-3];
                        }

                        sendReceive.write(total);
                    }
                   // String tempMsg=new String(readBuff,0,msg.arg1);
//                    if(tempMsg.equals("Received Successfully by user")){
//                        Toast.makeText(ReceiveScreen.this,tempMsg,Toast.LENGTH_LONG).show();
//                        break;
//                    }


//                    textView.setText(String.valueOf("Successfully Received " + tempMsg));
//                //    Toast.makeText(ReceiveScreen.this,"Received " + tempMsg , Toast.LENGTH_LONG).show();

                    break;
            }
            return true;
        }
    });

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

        public void run()
        {
            try {


                sendReceive=new SendReceive(socket);
                sendReceive.start();


//                String userId="bbb";
//                byte[] msg=userId.getBytes();
//                final byte[] name=new byte[3+msg.length];
//                name[0]=0;
//                name[1]=0;
//                name[2]=0;
//                for(int i=3;i<name.length;i++){
//                    name[i]=msg[i-3];
//                }
//                sendReceive.write(name);



            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
                inputStream=tempIn;
                outputStream=tempOut;
                boolean authenticationStatus = authenticate();
                System.out.println("Device authStatus: "+authenticationStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);

                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




        private void sendOwnCertificate(X509Certificate cert) throws Exception{
            byte[] certBytes = cert.getEncoded();
            System.out.println("Sent Certificate : "+ Arrays.hashCode(certBytes));
            outputStream.write(certBytes);
        }

        private X509Certificate receiveOthersCertificate() throws Exception{
            byte[] recCertBytes = new byte[2048];
            X509Certificate othersCertificate;
            int size=0;
            while (true) {
                System.out.println(inputStream.available());
                if (inputStream.available() > 0) {
                    size = inputStream.read(recCertBytes);
                    break;
                } else {
                    SystemClock.sleep(100);
                }
            }
            System.out.println("receivedCertificate : "+ Arrays.hashCode(recCertBytes));
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
            } catch(Exception e){
                e.printStackTrace();
                System.out.println("Certificate invalid!");
                return false;
            }
            System.out.println("Certificate is valid!");
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
            byte[] e = encryptRSA(chalConcatKeyToBeSent, othersPubKey);
            System.out.println("Size after encryption: "+e.length);

            outputStream.write(e);

            byte[] er = new byte[e.length];
            int size2 = inputStream.read(er);



            byte[] chalConcatKeyReceived;

            if(size2>0){

                chalConcatKeyReceived = decryptRSA(er, myPrivatekey);

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

                System.out.println("Responsed sent...");
                byte[] buffer = new byte[responseToBeSent.length];
                int size4 = inputStream.read(buffer);
                if(size4>0){
                    System.out.println("Response recvfd...");
                    byte[] responseToVerify = decryptAES(buffer, sharedSecretKey);
                    System.out.println("Response received");
                    for(int i = 0;i<responseToVerify.length;i++){
                        System.out.println(responseToVerify[i]);
                        if(responseToVerify[i] != challengeToBeSent[i]){
                            System.out.println("Different response!");
                            return false;
                        }
                    }
                    return true;
                }
            }

            return false;
        }







        public void write(byte[] bytes)
        {
            try {

                outputStream.write(encryptAES(bytes,sharedSecretKey));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(ReceiveScreen.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

    public byte[] encryptRSA(byte[] msg, PublicKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(msg);
    }

    public byte[] decryptRSA(byte[] ci, PrivateKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ci);
    }


    public byte[] encryptAES(byte[] msg, SecretKey secretKey) throws Exception{
        String initVector = "encryptionIntVec";
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(msg);
    }


    public byte[] decryptAES(byte[] encrypted, SecretKey secretKey) throws Exception{
        String initVector = "encryptionIntVec";
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);


        return cipher.doFinal(encrypted);
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
    }*/



}
