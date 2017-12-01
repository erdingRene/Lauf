package de.simon.brandhuber.lauf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rie on 01.12.2017.
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
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Table_Name + "( " + Col_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                    Col_2 + " INTEGER,"+
                                                    Col_3 + " TEXT," +
                                                    Col_4 + " DOUBLE," +
                                                    Col_5 + " DOUBLE," +
                                                    Col_6 + " DOUBLE," +
                                                    Col_7 + " LONG)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
    onCreate(db);
    }
}
