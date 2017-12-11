package de.simon.brandhuber.lauf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

    public Cursor getColumns(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT RUN_NUMBER,RUN_NAME FROM " + Table_Name, null);
            return res;
    }
    public String[] getColumnsString(){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT DISTINCT RUN_NUMBER,RUN_NAME FROM " + Table_Name, null);
        cursor.moveToFirst();
        ArrayList<String> runs = new ArrayList<String>();
        while(!cursor.isAfterLast()) {
            runs.add(cursor.getString(cursor.getColumnIndex("RUN_NUMBER")) + " " + cursor.getString(cursor.getColumnIndex("RUN_NAME")));


            cursor.moveToNext();
        }
        cursor.close();
        return runs.toArray(new String[runs.size()]);
    }


    public Integer deleteData(String RUN_NAME){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Table_Name,"RUN_NAME = ?", new String[]{RUN_NAME});
    }

    public  Integer howOftenExistsRunNumber (Integer runNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(RUN_NUMBER) as Number FROM " + Table_Name + " where RUN_Number = " + runNumber,null);
        res.moveToFirst();
        int howOftenIsRunNumber = res.getInt(res.getColumnIndex("Number"));
        return howOftenIsRunNumber;
    }

    public Integer idCounter (Integer runNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor zes = db.rawQuery("select ID  as Number from tbl_run where RUN_NUMBER = " + runNumber + " order by ID limit 1",null);
        zes.moveToFirst();
        Integer ID = zes.getInt(zes.getColumnIndex("Number"));

        return ID;
    }

    public Integer idCounterCounted (Integer runNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor zes = db.rawQuery("SELECT COUNT(ID) as Number FROM " + Table_Name + " where RUN_Number = " + runNumber,null);
        zes.moveToFirst();
        Integer ID = zes.getInt(zes.getColumnIndex("Number"));

        return ID;
    }

    public Double[] dataForDrawLine (Integer ID){


        Double[] LatLon2;
        LatLon2 = new Double[4];

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT LAT  as Number FROM " + Table_Name + " where ID = " + ID,null);
        res.moveToFirst();
        LatLon2[0] = res.getDouble(res.getColumnIndex("Number"));
        Cursor ces = db.rawQuery("SELECT LON  as Number FROM " + Table_Name + " where ID = " + ID,null);
        ces.moveToFirst();
        LatLon2[1] = ces.getDouble(ces.getColumnIndex("Number"));
        Cursor aes = db.rawQuery("SELECT LAT  as Number FROM " + Table_Name + " where ID = " + (ID + 1),null);
        aes.moveToFirst();
        LatLon2[2] = aes.getDouble(aes.getColumnIndex("Number"));
        Cursor bes = db.rawQuery("SELECT LON  as Number FROM " + Table_Name + " where ID = " + (ID + 1),null);
        bes.moveToFirst();
        LatLon2[3] = bes.getDouble(bes.getColumnIndex("Number"));
        return LatLon2;

    }
    }


