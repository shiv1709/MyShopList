package com.example.shiv.itemstobuy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.shiv.itemstobuy.model.Item;
import com.example.shiv.itemstobuy.util.Constants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context context;
    private final String TAG = "DatabaseHandler";

    //This constructor will create Database with passed name as argument
    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DB_VERSION);
        this.context = context;
        // SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_ITEM_TABLE = "CREATE TABLE " +
                Constants.TABLE_NAME + "( "
                + Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.KEY_ITEMNAME + " TEXT NOT NULL , "
                + Constants.KEY_ITEMQTY + " TEXT NOT NULL , "
                + Constants.KEY_ITEMBRAND + " TEXT DEFAULT NULL , "
                + Constants.KEY_ITEMCOLOR + " TEXT DEFAULT NULL , "
                + Constants.KEY_ITEMSIZE + " TEXT DEFAULT NULL , "
                + Constants.KEY_DATE_NAME + " LONG );";

        sqLiteDatabase.execSQL(CREATE_ITEM_TABLE);
        Log.d(TAG, " Table created");

       /* sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_NAME +
                " (ID INTEGER PRIMAR KEY,NAME TEXT NOT NULL,QUANTITY TEXT NOT NULL,BRAND TEXT DEFAULT NULL ," +
                "COLOR TEXT DEFAULT NULL,SIZE TEXT DEFAULT NULL,TYPE TEXT DEFAULT NULL);");*/
        //sqLiteDatabase.execSQL(("DROP TABLE " + TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(sqLiteDatabase);
        Log.d(TAG, " Table upgraded");
    }

    public boolean insertItemIntoDb(Item item) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.KEY_ITEMNAME, item.getItemName());
        contentValues.put(Constants.KEY_ITEMQTY, item.getItemQuantity()); //put takes two arguments,1st -column name in which you want to insert,2nd - value that you want to insert
        contentValues.put(Constants.KEY_ITEMBRAND, item.getItemBrand());
        contentValues.put(Constants.KEY_ITEMCOLOR, item.getItemColor());
        contentValues.put(Constants.KEY_ITEMSIZE, item.getItemSize());
        contentValues.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());

        //Insert the row or item data
        long result = sqLiteDatabase.insert(Constants.TABLE_NAME, null, contentValues);//insert takes three argument

        if (result == -1) {
            Log.d(TAG, " Item unable to insert into Db");
            return false;
        } else {
            Log.d(TAG, " Item successfully inserted to DB");
            return true;
        }

    }

    //Get a Item From Db
    public Item getItemFromDb(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_ITEMNAME,
                        Constants.KEY_ITEMQTY,
                        Constants.KEY_ITEMBRAND,
                        Constants.KEY_ITEMCOLOR,
                        Constants.KEY_ITEMSIZE,
                        Constants.KEY_DATE_NAME},
                Constants.KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Item item = new Item();
        if (cursor != null) {
            item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            item.setItemName(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMNAME)));
            item.setItemQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMQTY)));
            item.setItemBrand(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMBRAND)));
            item.setItemColor(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMCOLOR)));
            item.setItemSize(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMSIZE)));

            //converting TimeStamp to something readable

            DateFormat dateFormat = DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());
            item.setDateItemAdded(formattedDate);


        }

        return item;
    }

    //Get all items from Db

    public List<Item> getAllItemsFromDb() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        List<Item> itemList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_ITEMNAME,
                        Constants.KEY_ITEMQTY,
                        Constants.KEY_ITEMBRAND,
                        Constants.KEY_ITEMCOLOR,
                        Constants.KEY_ITEMSIZE,
                        Constants.KEY_DATE_NAME},
                null,
                null, null, null, Constants.KEY_DATE_NAME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                item.setItemName(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMNAME)));
                item.setItemQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMQTY)));
                item.setItemBrand(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMBRAND)));
                item.setItemColor(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMCOLOR)));
                item.setItemSize(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEMSIZE)));

                //converting TimeStamp to something readable

                DateFormat dateFormat = DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());
                item.setDateItemAdded(formattedDate);

                //Add to arraylist
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        return itemList;
    }

    // updateItem

    public boolean updateItemIntoDb(
            Item item) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.KEY_ITEMNAME, item.getItemName());
        contentValues.put(Constants.KEY_ITEMQTY, item.getItemQuantity()); //put takes two arguments,1st -column name in which you want to insert,2nd - value that you want to insert
        contentValues.put(Constants.KEY_ITEMBRAND, item.getItemBrand());
        contentValues.put(Constants.KEY_ITEMCOLOR, item.getItemColor());
        contentValues.put(Constants.KEY_ITEMSIZE, item.getItemSize());

        /*
        Update function takes four arguments
        1) Table name
        2)Updated values
        3)Condition for update
        4)String array
         */
        long result = sqLiteDatabase.update(Constants.TABLE_NAME, contentValues,
                Constants.KEY_ID + "=?", new String[]{String.valueOf(item.getId())});

        if (result == -1)
            return false;
        else
            return true;
    }

    // delete an item from Db
    public Integer deleteItemFromDb(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Integer rows_affected;
        rows_affected = sqLiteDatabase.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?", new String[]{String.valueOf(id)});//Here Integer return type of deleteData function bcoz delete function returns number of rows affected

        return rows_affected;
    }


    // get All Item Count From Db

    public int getItemCount() {

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);

        return cursor.getCount();
    }

}
