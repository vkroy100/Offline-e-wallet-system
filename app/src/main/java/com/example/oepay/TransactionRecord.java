package com.example.oepay;

import java.io.Serializable;

public class TransactionRecord implements Serializable {

    String senderUserId;
    String recptUserId;
    String  amount;
    String date;

    TransactionRecord(String senderUserId, String recptUserId, String amount, String date){
        this.amount=amount;
        this.date=date;
        this.senderUserId=senderUserId;
        this.recptUserId=recptUserId;
    }

//    public String getUserId() {
//        return senderUserId;
//    }
//
//    public void setUserId(String userId) {
//        this.senderUserId = userId;
//    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRecptUserId(String recptUserId) {
        this.recptUserId = recptUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getRecptUserId() {
        return recptUserId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }
}
