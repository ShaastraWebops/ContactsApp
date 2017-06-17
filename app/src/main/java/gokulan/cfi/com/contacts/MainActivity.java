package gokulan.cfi.com.contacts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import gokulan.cfi.com.contacts.LocalDB.CoreContract;
import gokulan.cfi.com.contacts.LocalDB.CoreDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String result = "initial";
        final String API_KEY = getString(R.string.SHEETS_API_KEY);
        final String SPREADSHEET_ID = "1_cY0ak-Q7XpB3X5tV9xtZMeOp5hIZsySwRXDCEzz6dY";
        //String url = "https://docs.google.com/spreadsheets/d/1aTRfgVXj_vj1pwxHLzkArx4ibRc2ayOwI1Q5ajkKXnM/pub?output=tsv"; //TESTING URL
        //String url = "https://docs.google.com/spreadsheets/d/1DOGxIrinXLGfsIgj27x-JsOZydNY8GXpSlzZoTiEmYo/pub?output=tsv";
        String url = "https://sheets.googleapis.com/v4/spreadsheets/"+SPREADSHEET_ID+"/values/CoCAS&Cores!A2:G100?key="+API_KEY;
        try {
            result = new HttpGetRequest().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = "null";
        }


        Log.i("testingresources", API_KEY);


        OutputStreamWriter oStreamWriter = null;
        try {
            oStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("index.txt", getApplicationContext().MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<Core> cores = new ArrayList<>();
        if(result != null){
            Log.i("Result",result);

            parseCores(cores,result);
            addCoresToLocal(cores);

        }else{
            readCoresFromLocal(cores);
        }



        try {
            oStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        CoreAdapter adapter = new CoreAdapter(MainActivity.this, cores);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void parseCores(ArrayList<Core> cores, String result) {

        try {
            JSONObject res = new JSONObject(result);
            JSONArray vals = res.getJSONArray("values");
            /*
            format for an element of vals:
            [dept-0,name-1,rollno-2,hostel-3,roomno-4,phones-5,emails-6]
             */
            String dept = "";
            for(int i=0;i<vals.length();i++){
                JSONArray corejson = vals.getJSONArray(i);

                if(corejson.length()==0)continue;

                // Check if same dept or new dept for the next core
                if(!corejson.getString(0).equals(""))dept = corejson.getString(0);

                Core c = new Core(corejson.getString(1),corejson.getString(2),dept);

                String phones = corejson.getString(5);
                String[] allphones = phones.split(",");
                for(int j=0;j<allphones.length;j++){
                    c.addPhone(allphones[j].trim());
                }

                String emails = corejson.getString(6);
                String[] allemails = emails.split(",");
                for(int j=0;j<allemails.length;j++){
                    c.addEmail(allemails[j].trim());
                }

                cores.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void readCoresFromLocal(ArrayList<Core> cores) {
        CoreDbHelper coreDbHelper = new CoreDbHelper(this);
        SQLiteDatabase db2 = coreDbHelper.getReadableDatabase();
        Cursor cursor = db2.query(CoreContract.CoreEntry.TABLE_NAME, null, null, null, null, null, null);
        Toast.makeText(this, "No internet, found "+cursor.getCount()+" contacts in cache", Toast.LENGTH_SHORT).show();

        while(cursor.moveToNext()){
            // Create a core
            Core c = new Core(cursor.getString(cursor.getColumnIndex(CoreContract.CoreEntry.COLUMN_NAME_CORE_NAME)),
                    cursor.getString(cursor.getColumnIndex(CoreContract.CoreEntry.COLUMN_NAME_ROLL_NUM)),
                    cursor.getString(cursor.getColumnIndex(CoreContract.CoreEntry.COLUMN_NAME_DEPT)));

            // Get all the phone numbers
            String phones = cursor.getString(cursor.getColumnIndex(CoreContract.CoreEntry.COLUMN_NAME_PHONES));
            String[] allphones = phones.split(",");
            for(int i=0;i<allphones.length;i++){
                c.addPhone(allphones[i].trim());
            }

            // Get all the emails
            String emails = cursor.getString(cursor.getColumnIndex(CoreContract.CoreEntry.COLUMN_NAME_EMAILS));
            String[] allemails = emails.split(",");
            for(int i=0;i<allemails.length;i++){
                c.addEmail(allemails[i].trim());
            }

            // Add the core to the arraylist
            cores.add(c);
        }

    }

    private void addCoresToLocal(ArrayList<Core> cores) {
        CoreDbHelper coreDbHelper = new CoreDbHelper(this);
        SQLiteDatabase db = coreDbHelper.getWritableDatabase();
        coreDbHelper.onUpgrade(db,0,1);

        for(int i=0;i<cores.size();i++){
            Core c = cores.get(i);
            ContentValues coreEntry = new ContentValues();

            // Add the name, rollnum and department
            coreEntry.put(CoreContract.CoreEntry.COLUMN_NAME_CORE_NAME,c.getName());
            coreEntry.put(CoreContract.CoreEntry.COLUMN_NAME_ROLL_NUM,c.getRollNum());
            coreEntry.put(CoreContract.CoreEntry.COLUMN_NAME_DEPT,c.getDepartment());

            // Store all the phone numbers as one comma separated string
            ArrayList<String> phones = c.getPhones();
            String allPhones = phones.get(0);
            for(int j=1;j<phones.size();j++){
                allPhones+=","+phones.get(j);
            }
            coreEntry.put(CoreContract.CoreEntry.COLUMN_NAME_PHONES, allPhones);

            // Store all the emails as one comma separated string
            ArrayList<String> emails = c.getEmails();
            String allEmails = emails.get(0);
            for(int j=1;j<emails.size();j++){
                allEmails+=","+emails.get(j);
            }
            coreEntry.put(CoreContract.CoreEntry.COLUMN_NAME_EMAILS, allEmails);

            // Add the entry to the database
            db.insert(CoreContract.CoreEntry.TABLE_NAME, null, coreEntry);
        }

        SQLiteDatabase db2 = coreDbHelper.getReadableDatabase();
        Cursor cursor = db2.query(CoreContract.CoreEntry.TABLE_NAME, null, null, null, null, null, null);
        Toast.makeText(this, "Stored "+cursor.getCount()+" contacts in cache", Toast.LENGTH_SHORT).show();
    }
}
