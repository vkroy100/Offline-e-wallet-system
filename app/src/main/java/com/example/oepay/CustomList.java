package com.example.oepay;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private String[] senderUserId;
    private String[] recptUserId;
    private String[] date;
    private int[] amount;
    private int[] transactionId;
    private String[] signature;
    public CustomList( Activity context, String[] senderUserId, String[] recptUserId, String[] date, int[] amount, int[] transactionId, String[] signature) {
        super(context,R.layout.transaction_history, senderUserId);
        this.context =  context;
        this.amount=amount;
        this.date=date;
        this.senderUserId=senderUserId;
        this.recptUserId = recptUserId;
        this.transactionId=transactionId;
        this.signature = signature;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.transaction_history, null, true);

        TextView txtRUserId = rowView.findViewById(R.id.paidTo);
        txtRUserId.setText(recptUserId[position]);

        TextView txtSUserId = rowView.findViewById(R.id.paidFrom);
        txtSUserId.setText(senderUserId[position]);

        TextView txtDate=rowView.findViewById(R.id.date_time);
        txtDate.setText(date[position]);

        TextView textView=rowView.findViewById(R.id.amountPaid);
        textView.setText(String.valueOf(amount[position]));

        TextView textTransactionId=rowView.findViewById(R.id.TransactionId);
        textTransactionId.setText(String.valueOf(transactionId[position]));

        TextView signs=rowView.findViewById(R.id.signs);
        signs.setText(signature[position]);

        return rowView;
    }
}
