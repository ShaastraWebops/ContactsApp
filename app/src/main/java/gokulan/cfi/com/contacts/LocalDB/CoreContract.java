package gokulan.cfi.com.contacts.LocalDB;

import android.provider.BaseColumns;

/**
 * Created by Rajat on 09-06-2017.
 */

public final class CoreContract {
    private void CoreContract(){}

    public static class CoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "cores";
        public static final String COLUMN_NAME_CORE_NAME = "coreName";
        public static final String COLUMN_NAME_ROLL_NUM = "rollNum";
        public static final String COLUMN_NAME_DEPT = "dept";
        public static final String COLUMN_NAME_EMAILS = "emails";
        public static final String COLUMN_NAME_PHONES = "phones";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CoreEntry.TABLE_NAME + " (" +
                    CoreEntry._ID + " INTEGER PRIMARY KEY," +
                    CoreEntry.COLUMN_NAME_CORE_NAME + " TEXT," +
                    CoreEntry.COLUMN_NAME_ROLL_NUM + " TEXT," +
                    CoreEntry.COLUMN_NAME_DEPT + " TEXT," +
                    CoreEntry.COLUMN_NAME_EMAILS + " TEXT," +
                    CoreEntry.COLUMN_NAME_PHONES + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CoreEntry.TABLE_NAME;
    
}
