package org.cri.deliveryrecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "itemManager";
    private static final String TABLE_ITEMS = "items";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DEL_TIM = "delivery_time";
    private static Context _context;
    private static int cacheTot;
    private static int _deliveryPointSelection;


    private static ItemDatabaseHandler _Item_DatabaseHandler = null;

    public static ItemDatabaseHandler getInstance(Context context)
    {
        if (_Item_DatabaseHandler == null){
            _Item_DatabaseHandler = new ItemDatabaseHandler(context);
            _context = context;
        }

        return _Item_DatabaseHandler;
    }

    public static ItemDatabaseHandler getInstance()
    {
        if (_Item_DatabaseHandler == null)
            _Item_DatabaseHandler = new ItemDatabaseHandler(_context);

        return _Item_DatabaseHandler;
    }

    private ItemDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DEL_TIM + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
     //   db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        // Create tables again
       // onCreate(db);
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addContact(Guest guest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, guest.getID()); // Contact ID
        values.put(KEY_DEL_TIM, guest.getDeliveryTime().toString());

        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
        cacheTot = cacheTot + 1;
    }

    // code to get the single contact
    Guest getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Guest guest = null;

        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ID,
                        KEY_NAME, KEY_DEL_TIM }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor.moveToFirst()) {
            guest = new Guest(Integer.parseInt(cursor.getString(0)), cursor.getString(2));
        }
        // return contact
        cursor.close();
        db.close();

        return guest;
    }

    // code to get all contacts in a list view
    public List<Guest> getAllContacts() {
        List<Guest> guestList = new ArrayList<Guest>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " ORDER BY " + KEY_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Guest guest = new Guest();
                guest.setID(Integer.parseInt(cursor.getString(0)));
                guest.setName(cursor.getString(1));
                guest.setDeliveryTime(cursor.getString(2));
                // Adding contact to list
                guestList.add(guest);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return guestList;
    }

    // code to update the single contact
    public int updateContact(Guest guest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, guest.getName());
        values.put(KEY_DEL_TIM, guest.getDeliveryTime());

        db.close();
        db.update(TABLE_ITEMS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(guest.getID()) });

        return 0;
    }

    // Deleting single contact
    public void deleteContact(Guest guest) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[] { String.valueOf(guest.getID()) });
        db.close();
        cacheTot = cacheTot - 1;
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int tot = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        cacheTot = tot;
        return tot;
    }

    public List<String> getMissingID(int delPoint) {
        List<String> guestList = new ArrayList<String>();
        String selectQuery = "SELECT map.id FROM map LEFT JOIN items ON map.id = items.id WHERE (items.id IS NULL) AND (map.deliveryPoint = '" +
                 delPoint + "')";

       SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                guestList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // return contact list
        db.close();
        return guestList;
    }


        public int getTot(){
        return cacheTot;
        }

    public int getDeliveryPointSelection() {
        return _deliveryPointSelection;
    }

    public void setDeliveryPointSelection(int selection) {
        _deliveryPointSelection = selection;
    }
}
