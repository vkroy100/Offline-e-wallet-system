package com.example.oepay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TransactionDB";
    private static final String TABLE_NAME = "Transactions";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDERUSERID = "senderUserId";
    private static final String KEY_RECPTUSERID = "recptUserId";
    private static final String KEY_DATE = "date";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_SIGNATURE = "signature";
    private static final String[] COLUMNS = { KEY_ID,  KEY_DATE, KEY_SENDERUSERID, KEY_RECPTUSERID,
            KEY_AMOUNT, KEY_SIGNATURE };


    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Transactions ( "
                + "id INTEGER PRIMARY KEY, " + "date TEXT, "
                + "senderUserId TEXT, " + "recptUserId TEXT, " + "amount INTEGER, " + "signature TEXT )";

        db.execSQL(CREATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }
    public void deleteOne(Transaction transaction) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(transaction.getId()) });
        db.close();
    }

    public SignedTransaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Transaction transaction = new Transaction();
        transaction.setId(Integer.parseInt(cursor.getString(0)));
        transaction.setDate(cursor.getString(1));
        transaction.setSenderUserId(cursor.getString(2));
        transaction.setRecptUserId(cursor.getString(3));
        transaction.setAmount(Integer.parseInt(cursor.getString(4)));
        String sign = cursor.getString(5);

        SignedTransaction signedTransaction = new SignedTransaction(transaction);
        signedTransaction.setSignature(sign.getBytes());

        return signedTransaction;
    }
    public List<SignedTransaction> allTransactions() {

        List<SignedTransaction> transactions = new LinkedList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Transaction transaction = null;

        if (cursor.moveToFirst()) {
            do {
                transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setDate(cursor.getString(1));
                transaction.setSenderUserId(cursor.getString(2));
                transaction.setRecptUserId(cursor.getString(3));
                transaction.setAmount(Integer.parseInt(cursor.getString(4)));
                String sign = cursor.getString(5);
                SignedTransaction signedTransaction = new SignedTransaction(transaction);
                signedTransaction.setSignature(sign.getBytes());
                transactions.add(signedTransaction);
            } while (cursor.moveToNext());
        }

        return transactions;
    }
    public void addtransaction(SignedTransaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, transaction.getDate());
        values.put(KEY_SENDERUSERID, transaction.getSenderUserId());
        values.put(KEY_RECPTUSERID, transaction.getRecptUserId());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_SIGNATURE, transaction.getSignature());
        values.put(KEY_ID,transaction.getId());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }
    public int updateTransaction(SignedTransaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, transaction.getDate());
        values.put(KEY_SENDERUSERID, transaction.getSenderUserId());
        values.put(KEY_RECPTUSERID, transaction.getRecptUserId());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_SIGNATURE, transaction.getSignature());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(transaction.getId()) });

        db.close();

        return i;
    }
}
