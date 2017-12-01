package de.simon.brandhuber.lauf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by René on 01.12.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String Database_Name = "run.db";
    public static final String Table_Name = "tbl_run";
    public static final String Col_1 = "ID";
    public static final String Col_2 = "RUN_NUMBER";
    public static final String Col_3 = "RUN_NAME";
    public static final String Col_4 = "LAT";
    public static final String Col_5 = "LON";
    public static final String Col_6 = "HIGHT";
    public static final String Col_7 = "DATETIME";



    public DatabaseHelper(Context context) {
        super(context, Database_Name, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String SQL_String = "CREATE TABLE " + Table_Name + "( " +
                        Col_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        Col_2 + " INTEGER,"+
                        Col_3 + " TEXT," +
                        Col_4 + " DOUBLE," +
                        Col_5 + " DOUBLE," +
                        Col_6 + " DOUBLE," +
                        Col_7 + " STRING" + ")";
    db.execSQL(SQL_String);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
    onCreate(db);
    }

    public boolean insertData(Integer runNumber, String runName, Double lat, Double lon, Double hight, String datetime){
        //Schreibt DB
        SQLiteDatabase db = this.getWritableDatabase();
        //Inhalt der in die DB geschrieben wird
        ContentValues contenValue = new ContentValues();
        contenValue.put(Col_2,runNumber);
        contenValue.put(Col_3,runName);
        contenValue.put(Col_4,lat);
        contenValue.put(Col_5,lon);
        contenValue.put(Col_6,hight);
        contenValue.put(Col_7,datetime);
        //Inhalt in die DB schreiben
        long  result = db.insert(Table_Name,null,contenValue);
        if (result == -1)
            return false;
        else
            return true;

        }

    public int lastRunNumber(){


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT max(RUN_NUMBER) as Number FROM tbl_run",null);
        res.moveToFirst();
        int maxRunNumber = res.getInt(res.getColumnIndex("Number"));
        return maxRunNumber;
    }

}
