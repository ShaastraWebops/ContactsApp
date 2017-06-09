package gokulan.cfi.com.contacts.LocalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rajat on 09-06-2017.
 */

public class CoreDbHelper extends SQLiteOpenHelper{


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cores.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CoreContract.CoreEntry.TABLE_NAME + " (" +
                    CoreContract.CoreEntry._ID + " INTEGER PRIMARY KEY," +
                    CoreContract.CoreEntry.COLUMN_NAME_CORE_NAME + " TEXT," +
                    CoreContract.CoreEntry.COLUMN_NAME_ROLL_NUM + " TEXT," +
                    CoreContract.CoreEntry.COLUMN_NAME_DEPT + " TEXT," +
                    CoreContract.CoreEntry.COLUMN_NAME_EMAILS + " TEXT," +
                    CoreContract.CoreEntry.COLUMN_NAME_PHONES + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CoreContract.CoreEntry.TABLE_NAME;



    public CoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
