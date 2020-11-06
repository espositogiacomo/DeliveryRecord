package org.cri.deliveryrecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MapDatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "itemManager";
    private static final String TABLE_ITEMS = "map";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "deliveryPoint";
    private static final String KEY_DEL_TIM = "note";
    private static Context _context;
    private int firstRun = 0;
    private boolean _updateInProgress = false;
    private int[] _partTot = {0,0,0,0,0,0};

    private static MapDatabaseHandler _Item_DatabaseHandler = null;

    public int[] getPartTot(){
        return _partTot;
    }

    public void setPartTot(int[] value){
        _partTot = value;
    }


    public boolean getUpdateInProgress(){
        return _updateInProgress;
    }

    public void setUpdateInProgress(boolean value){
        _updateInProgress = value;
    }


    public static MapDatabaseHandler getInstance(Context context)
    {
        if (_Item_DatabaseHandler == null){
            _Item_DatabaseHandler = new MapDatabaseHandler(context);
            _context = context;
        }

        return _Item_DatabaseHandler;
    }

    public static MapDatabaseHandler getInstance()
    {
        if (_Item_DatabaseHandler == null)
            _Item_DatabaseHandler = new MapDatabaseHandler(_context);

        return _Item_DatabaseHandler;
    }

    private MapDatabaseHandler(Context context) {
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

    public void deleteAllMap() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addMap(DelPointMap delPointMap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, delPointMap.getID()); // Contact ID
        values.put(KEY_NAME, delPointMap.getDeliveryPoint().toString());
        values.put(KEY_DEL_TIM, delPointMap.getNote().toString());
        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    DelPointMap getMap(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DelPointMap delPointMap = null;

        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ID,
                        KEY_NAME, KEY_DEL_TIM }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor.moveToFirst()) {
            delPointMap = new DelPointMap(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2));
        }
        // return contact
        cursor.close();
        db.close();

        return delPointMap;
    }

    public int getCountByDelPoint(int deliveryPoint){
        if (firstRun == 0) {
            firstRun++;
            return 0;
        }

        String countQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_NAME + " = '" + deliveryPoint +"'"  ;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int tot = cursor.getCount();
            cursor.close();
            db.close();
            return tot;

    }

}
