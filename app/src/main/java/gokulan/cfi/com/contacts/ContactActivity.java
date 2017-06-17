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

        Core c = (Core) getIntent().getSerializableExtra("coreObject");


        TextView name_view = (TextView) findViewById(R.id.name_view);
        TextView dept_view = (TextView) findViewById(R.id.dept_view);
        name_view.setText(c.getName());
        dept_view.setText(c.getDepartment());

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


}
