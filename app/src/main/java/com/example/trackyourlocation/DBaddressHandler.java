package com.example.trackyourlocation;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBaddressHandler  extends SQLiteOpenHelper {

    private static final int DATABSE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PRODUCTNAME = "productname";
    /*******/
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LONG = "longitude";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DURATION = "duration";

    public DBaddressHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query ="CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCTNAME + " TEXT, " +
                COLUMN_LAT +  " TEXT, "        +
                COLUMN_LONG + " TEXT, "        +
                COLUMN_DATE + " TEXT, "        +
                COLUMN_TIME + " TEXT, "        +
                COLUMN_DURATION + " TEXT "     +
                ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCTS);
        onCreate(db);

    }

    //ADD new product to the database
    //public  void addProduct (String s){
    public  void addUserPlace (String name , String latitude, String longitude ){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME,name);
        values.put(COLUMN_LAT , latitude);
        values.put(COLUMN_LONG , longitude);

        values.put(COLUMN_DATE,"0");
        values.put(COLUMN_TIME,"0");
        values.put(COLUMN_DURATION,"0");


        SQLiteDatabase db=getWritableDatabase();

        db.insert(TABLE_PRODUCTS,null,values);
        db.close();

    }

    public void addPlace(String latitude, String longitude , String date, String time ,String duration )
    {
        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCTNAME,"-");


        values.put( COLUMN_LAT , latitude);
        values.put(COLUMN_LONG, longitude);
        values.put(COLUMN_DATE,date);
        values.put(COLUMN_TIME,time);
        values.put(COLUMN_DURATION,duration);

        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLE_PRODUCTS,null,values);
        db.close();

    }

    //Delete aproduct from the database
    public void deleteProduct (String i){
        SQLiteDatabase db = getWritableDatabase();
        //      db.execSQL("DELETE FROM "+ TABLE_PRODUCTS+" WHERE "+ COLUMN_PRODUCTNAME +"=\""+i+"\";");

        String selection = COLUMN_PRODUCTNAME + " LIKE ?";
        String [] selectionArgs = { i };

        db.delete(TABLE_PRODUCTS,selection,selectionArgs);

    }

    //print out the database as string
    public String databseToString()
    {
        String dbString ="";
        SQLiteDatabase db=getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        //Cursor points to a location in your result
        Cursor c = db.rawQuery(query,null);

        /**************************************************/
        String[] projectoin = {COLUMN_ID,COLUMN_PRODUCTNAME,COLUMN_LAT,COLUMN_LONG,COLUMN_DATE,COLUMN_TIME,COLUMN_DURATION};
        String selection =COLUMN_PRODUCTNAME + " = ?";
        String[] selectionArgs = {"-"};
        String sortorder= COLUMN_ID + " DESC";

//
//
//
//      Cursor c =db.query( TABLE_PRODUCTS,projectoin,null,null,null,null,sortorder);

        // move to the frist raw in your result




        while (c.moveToNext()){

            dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
            dbString+= " - ";

            dbString+= c.getString(c.getColumnIndexOrThrow(COLUMN_PRODUCTNAME));
            dbString+= " ";
            if(c.getString(c.getColumnIndexOrThrow(COLUMN_PRODUCTNAME))!="-") {
                dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_LAT));
                dbString += " ";

                dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_LONG));
                dbString += " ";

                dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_DATE));
                dbString += " ";

                dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_TIME));
                dbString += " ";

                dbString += c.getString(c.getColumnIndexOrThrow(COLUMN_DURATION));
                dbString += "  \n";
            }

        /*    if(c.getString(c.getColumnIndex("productname"))!=null)
            {
                dbString+=c.getString(c.getColumnIndex("_id"));
                dbString+=" - ";
                dbString+=c.getString(c.getColumnIndex("productname"));
                dbString+="\n";
            }
            c.moveToNext();
            */
        }
        db.close();
        return dbString;
    }

}



