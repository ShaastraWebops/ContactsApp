package gokulan.cfi.com.contacts;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import gokulan.cfi.com.contacts.LocalDB.CoreContract;
import gokulan.cfi.com.contacts.LocalDB.CoreDbHelper;

public class ContactActivity extends AppCompatActivity {

    Core c;
    boolean isFavorite = true;
    Button addFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

         c = (Core) getIntent().getSerializableExtra("coreObject");

        isFavorite=false;

        Log.i("selfCore",c.getData());
        for(int i=0;i<HomeActivity.favorites.size();i++){
            Log.i("favs", HomeActivity.favorites.get(i).getData());
            if(c.isSame(HomeActivity.favorites.get(i)))isFavorite=true;
        }

        TextView name_view = (TextView) findViewById(R.id.name_view);
        TextView dept_view = (TextView) findViewById(R.id.dept_view);
        name_view.setText(c.getName());
        dept_view.setText(c.getDepartment());
        addFav = (Button) findViewById(R.id.addToFav);
        Log.i("isfav", String.valueOf(isFavorite));
        if(isFavorite){
            addFav.setText("Remove from Favorites");
        }else{
            addFav.setText("Add to Favorites");
        }

        addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFavorite){
                    removeFromFavorites();
                    isFavorite=false;
                }else{
                    addToFavorites();
                    isFavorite=true;
                }
            }
        });

        ArrayList<String> contacts = new ArrayList<>();
        ArrayList<String> vals1 = c.getEmails();

        for(int i=0; i < vals1.size(); i++)
        {
            contacts.add(vals1.get(i));
        }
        ArrayList<String> vals2 = c.getPhones();
        Log.e("cSize", String.valueOf(contacts.size()));
        Log.d("size", String.valueOf(vals2.size()));
        Log.d("val", vals2.get(0));
        for(int i=0; i<vals2.size(); i++)
        {
            contacts.add(vals2.get(i));
        }
        Log.d("Count", String.valueOf(contacts.size()));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);

        ContactAdapter adapter = new ContactAdapter(ContactActivity.this, contacts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }



    private void addToFavorites() {
        addFav.setText("Remove from Favorites");

        HomeActivity.favorites.add(c);

        CoreContract.CoreEntry.tableName=HomeActivity.FAVORITES_NAME;
        CoreDbHelper corehelper = new CoreDbHelper(this);
        SQLiteDatabase db = corehelper.getWritableDatabase();
        corehelper.onCreate(db);

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
        db.insert(CoreContract.CoreEntry.tableName, null, coreEntry);

    }

    private void removeFromFavorites() {
        addFav.setText("Add to Favorites");

        for(int i=0;i<HomeActivity.favorites.size();i++){
            if(c.isSame(HomeActivity.favorites.get(i))){
                HomeActivity.favorites.remove(i);
                i--;
            }
        }

        CoreContract.CoreEntry.tableName=HomeActivity.FAVORITES_NAME;
        CoreDbHelper corehelper = new CoreDbHelper(this);
        SQLiteDatabase db = corehelper.getWritableDatabase();

        String whereClause = CoreContract.CoreEntry.COLUMN_NAME_CORE_NAME+"=\""+c.getName()+"\" and "+
                CoreContract.CoreEntry.COLUMN_NAME_DEPT+"=\""+c.getDepartment()+"\" and "+
                CoreContract.CoreEntry.COLUMN_NAME_ROLL_NUM+"=\""+c.getRollNum()+"\"";
//        String[] whereArgs = new String[]{c.getName(),c.getDepartment(),c.getRollNum()};

//        Log.i("whereargs", Arrays.toString(whereArgs));
        int del = db.delete(CoreContract.CoreEntry.tableName,whereClause,null);
        Log.i("removeFromFav_Deleted",del+"");


    }

}
