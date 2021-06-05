package com.example.oepay;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.Signature;

public class SignedTransaction implements Serializable {
    Transaction transaction;
    byte[] signature;

    public SignedTransaction(Transaction transactionRecord){
        this.transaction = transactionRecord;
    }

    public void signTransactionRecord(PrivateKey privateKey) throws Exception{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(baos);
        out.writeObject(transaction);
        out.flush();

        byte[] trBytes = baos.toByteArray();

        baos.close();

        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(privateKey);
        sig.update(trBytes);

        signature = Base64.encode(sig.sign(),Base64.DEFAULT);

    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getDate(){
        return transaction.getDate();
    }

    public String getSenderUserId(){
        return transaction.getSenderUserId();
    }

    public String getRecptUserId() {
        return transaction.getRecptUserId();
    }

    public String getAmount() {
        return String.valueOf(transaction.getAmount());
    }

    public String getId() {
        return String.valueOf(transaction.getId());
    }

    public String getSignature() {
        return new String(signature);
    }
}
