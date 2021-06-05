package com.example.oepay;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int id;
    private String date;
    private String senderUserId;
    private String recptUserId;
    private int amount;

    Transaction(){

    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dte) {
        date = dte;
    }

//    public static String getUserId() {
//        return senderUserId;
//    }
//
//    public static void setUserId(String uId) {
//        senderUserId = uId;
//    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amt) {
        amount = amt;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setRecptUserId(String recptUserId) {
        this.recptUserId = recptUserId;
    }

    public String getRecptUserId() {
        return recptUserId;
    }
}
