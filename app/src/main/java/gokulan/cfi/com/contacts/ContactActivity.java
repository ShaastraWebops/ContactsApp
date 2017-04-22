package gokulan.cfi.com.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        String name = getIntent().getStringExtra("name");
        Log.d("NAME", name);
        String line = "";

        try {
            InputStream iStream = openFileInput("index.txt");
            InputStreamReader reader = new InputStreamReader(iStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            Boolean found = false;
            while(!found && ((line = bufferedReader.readLine()) != null))
            {
                Log.d("LINE", line);
                if(line.startsWith(name))
                {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("Line", line);
        String[] vals = line.split(",");

        TextView name_view = (TextView) findViewById(R.id.name_view);
        TextView dept_view = (TextView) findViewById(R.id.dept_view);
        name_view.setText(vals[0]);
        dept_view.setText(vals[2]);

        String emails = vals[4];
        String phones = vals[3];

        ArrayList<String> contacts = new ArrayList<>();
        String[] vals1 = emails.split("#");

        for(int i=0; i < vals1.length; i++)
        {
            contacts.add(vals1[i]);
        }
        String[] vals2 = phones.split("#");
        Log.e("cSize", String.valueOf(contacts.size()));
        Log.d("size", String.valueOf(vals2.length));
        Log.d("val", vals2[0]);
        for(int i=0; i<vals2.length; i++)
        {
            contacts.add(vals2[i]);
        }
        Log.d("Count", String.valueOf(contacts.size()));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);

        ContactAdapter adapter = new ContactAdapter(ContactActivity.this, contacts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


}
