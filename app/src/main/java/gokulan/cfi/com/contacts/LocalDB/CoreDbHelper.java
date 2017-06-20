package gokulan.cfi.com.contacts.LocalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rajat on 09-06-2017.
 */

public class CoreDbHelper extends SQLiteOpenHelper{


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cores.db";

    private String getSqlCreateEntries(){
        return "CREATE TABLE IF NOT EXISTS " + CoreContract.CoreEntry.tableName + " (" +
                CoreContract.CoreEntry._ID + " INTEGER PRIMARY KEY," +
                CoreContract.CoreEntry.COLUMN_NAME_CORE_NAME + " TEXT," +
                CoreContract.CoreEntry.COLUMN_NAME_ROLL_NUM + " TEXT," +
                CoreContract.CoreEntry.COLUMN_NAME_DEPT + " TEXT," +
                CoreContract.CoreEntry.COLUMN_NAME_EMAILS + " TEXT," +
                CoreContract.CoreEntry.COLUMN_NAME_PHONES + " TEXT)";
    }

    private String getSqlDeleteEntries(){
        return "DROP TABLE IF EXISTS " + CoreContract.CoreEntry.tableName;
    }

    public CoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("coredbhelperOnCreate", CoreContract.CoreEntry.tableName);
        db.execSQL(getSqlCreateEntries());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getSqlDeleteEntries());
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
