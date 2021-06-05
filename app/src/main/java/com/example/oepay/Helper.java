package com.example.oepay;

import javax.crypto.SecretKey;

public class Helper {


    private static String type;
    private static NearBySend.ServerClass serverClass;
    private static NearBySend.ClientClass clientClass;
    private static NearBySend.SendReceive sendReceive;
    private static SecretKey sharedSessionKey;
    private static String userId;

    private Helper(){

    }


    public static synchronized void setSharedSessionKey(SecretKey sharedSessionKey) {
        Helper.sharedSessionKey = sharedSessionKey;
    }

    public static synchronized void setUserId(String userId) {
        Helper.userId = userId;
    }

    public static synchronized void setType(String t){
        type = t;
    }

    public static synchronized void setServerClass(NearBySend.ServerClass s){
        serverClass = s;
    }

    public static synchronized void setClientClass(NearBySend.ClientClass c){
        clientClass = c;
    }

    public static synchronized void setSendReceive(NearBySend.SendReceive sr){
        sendReceive = sr;
    }

    public static synchronized SecretKey getSharedSessionKey() {
        return sharedSessionKey;
    }

    public static synchronized String getUserId() {
        return userId;
    }

    public static synchronized String getType(){
        return type;
    }

    public static synchronized NearBySend.ServerClass getServerClass(){
        return serverClass;
    }

    public static synchronized NearBySend.ClientClass getClientClass(){
        return clientClass;
    }

    public static synchronized NearBySend.SendReceive getSendReceive(){
        return sendReceive;
    }

}
