package com.example.oepay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

public class TransactionHistory extends AppCompatActivity {
    private int count=0;
    private ListView transactionHistory;
    private SQLiteDatabaseHandler db;
    static boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        transactionHistory=findViewById(R.id.transactionHistory);
        flag=false;
        db=new SQLiteDatabaseHandler(this);
//        if(count==0) {
//            Transaction transaction1 = new Transaction(1, "20/10/2019 8:20 AM", "abc123", 203);
//            Transaction transaction2 = new Transaction(2, "20/10/2019 8:20 AM", "cde123", 208);
//            Transaction transaction3 = new Transaction(3, "20/10/2019 8:20 AM", "def123", 214);
//            db.addtransaction(transaction1);
//            db.addtransaction(transaction2);
//            db.addtransaction(transaction3);
//            count++;
//        }
        List<SignedTransaction> transactions=db.allTransactions();
        if(transactions!=null){
            String[] date=new String[transactions.size()];
            String []senderUserId=new String[transactions.size()];
            String [] recptUserId=new String[transactions.size()];
            int []amount=new int[transactions.size()];
            int []transactionId=new int[transactions.size()];
            String [] signature = new String[transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                date[i] = transactions.get(i).getDate();
                senderUserId[i]=transactions.get(i).getSenderUserId();
                recptUserId[i]=transactions.get(i).getRecptUserId();
                amount[i]=Integer.parseInt(transactions.get(i).getAmount());
                transactionId[i]=Integer.parseInt(transactions.get(i).getId());
                signature[i]=transactions.get(i).getSignature();
            }
           // Toast.makeText(TransactionHistory.this,String.valueOf(transactionId[0]),Toast.LENGTH_LONG).show();
            CustomList adapter=new CustomList(TransactionHistory.this,senderUserId, recptUserId, date,amount,transactionId, signature);
            transactionHistory.setAdapter(adapter);

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(TransactionHistory.this,MainActivity.class);
                startActivity(intent);
                // app icon in action bar clicked; go home
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(TransactionHistory.this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            flag=false;
            Intent intent = new Intent(TransactionHistory.this, LoginReg.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        flag=true;
    }

}
