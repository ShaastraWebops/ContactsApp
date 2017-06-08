package gokulan.cfi.com.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String result = "initial";
        String url = "https://docs.google.com/spreadsheets/d/1aTRfgVXj_vj1pwxHLzkArx4ibRc2ayOwI1Q5ajkKXnM/pub?output=tsv";
        //String url = "https://docs.google.com/spreadsheets/d/1DOGxIrinXLGfsIgj27x-JsOZydNY8GXpSlzZoTiEmYo/pub?output=tsv";
        try {
            result = new HttpGetRequest().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = "null";
        }


        if(result == null){
            Log.i("RESULTNULL","prob no internet");
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("Result",result);


        OutputStreamWriter oStreamWriter = null;
        try {
            oStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("index.txt", getApplicationContext().MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<Core> cores = new ArrayList<>();
        String[] people = result.split("\n");
        String dept = "";
        Core core;
        for(int i=1; i<people.length; i++)
        {
            String line = people[i];
            //Log.d("line", line.trim())
            String[] vals = line.split("\t");
            if(vals.length!=0)
            {
                if(!line.startsWith("\t"))
                {
                    core = new Core(vals[1], vals[2], vals[0]);
                    dept = vals[0];
                    String p = vals[5];
                    String ps[] = p.split(",");
                    for(int j=0; j<ps.length; j++)
                    {
                        core.addPhone(ps[j].trim());
                    }
                    p = vals[6];
                    ps = p.split(",");
                    for(int j=0; j<ps.length; j++) {
                        core.addEmail(ps[j].trim());
                    }
                }
                else
                {
                    core = new Core(vals[1], vals[2], dept);
                    String p = vals[5];
                    String ps[] = p.split(",");
                    for(int j=0; j<ps.length; j++)
                    {
                        core.addPhone(ps[j].trim());
                    }
                    p = vals[6];
                    ps = p.split(",");
                    for(int j=0; j<ps.length; j++) {
                        core.addEmail(ps[j].trim());
                    }
                }
                cores.add(core);
                Log.e("DETAILS", core.getData());
                try {
                    oStreamWriter.write(core.getData()+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
}
