package gokulan.cfi.com.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import gokulan.cfi.com.contacts.LocalDB.CoreContract;
import gokulan.cfi.com.contacts.LocalDB.CoreDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CoreListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CoreListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoreListFragment extends Fragment implements ExecuteDoneCallback {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_VERTICAL = "vertical";

    private String vertical;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    public CoreListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vvertical Vertical of the contacts.
     * @return A new instance of fragment CoreListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoreListFragment newInstance(String vvertical) {
        CoreListFragment fragment = new CoreListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VERTICAL, vvertical);
        Log.i("gotvvertical",vvertical);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_core_list, container, false);
        if (getArguments() != null) {
            vertical = getArguments().getString(ARG_VERTICAL);
            Log.i("gotvertical",vertical);
        }


        ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.main_progressBar);

        final String API_KEY = getString(R.string.SHEETS_API_KEY);
        final String SPREADSHEET_ID = "1_cY0ak-Q7XpB3X5tV9xtZMeOp5hIZsySwRXDCEzz6dY";
        //String url = "https://docs.google.com/spreadsheets/d/1aTRfgVXj_vj1pwxHLzkArx4ibRc2ayOwI1Q5ajkKXnM/pub?output=tsv"; //TESTING URL
        //String url = "https://docs.google.com/spreadsheets/d/1DOGxIrinXLGfsIgj27x-JsOZydNY8GXpSlzZoTiEmYo/pub?output=tsv";
        String url = "https://sheets.googleapis.com/v4/spreadsheets/"+SPREADSHEET_ID+"/values/"+vertical+"!A2:G100?key="+API_KEY;
        new HttpGetRequest(pb, this).execute(url);


        Log.i("testingresources", API_KEY);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        //Toast.makeText(getActivity(), "TEST", Toast.LENGTH_SHORT).show();

        return v;
    }

    private String parseName(String vertical) {
        String parsed = "";
        char c;
        for(int i=0;i<vertical.length();i++){
            c = vertical.charAt(i);
            if(Character.isLetterOrDigit(c)){
                parsed+=c;
            }
        }
        return parsed;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void executeDoneCallback(String result) {
        CoreContract.CoreEntry.tableName=parseName(vertical);

        ArrayList<Core> cores = new ArrayList<>();
        if(result != null){
            Log.i("Result",result);

            parseCores(cores,result);
            addCoresToLocal(cores);

        }else{
            readCoresFromLocal(cores);
        }

        

        CoreAdapter adapter = new CoreAdapter(getActivity(), cores);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
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
        CoreDbHelper coreDbHelper = new CoreDbHelper(this.getContext());
        SQLiteDatabase db2 = coreDbHelper.getReadableDatabase();
        Cursor cc = db2.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + CoreContract.CoreEntry.tableName + "'", null);
        if(cc.getCount()==0){
            Toast.makeText(this.getContext(), "No internet, found "+cc.getCount()+" contacts in cache", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db2.query(CoreContract.CoreEntry.tableName, null, null, null, null, null, null);
        Toast.makeText(this.getContext(), "No internet, found "+cursor.getCount()+" contacts in cache", Toast.LENGTH_SHORT).show();

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
        CoreDbHelper coreDbHelper = new CoreDbHelper(this.getContext());
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
            db.insert(CoreContract.CoreEntry.tableName, null, coreEntry);
        }

        SQLiteDatabase db2 = coreDbHelper.getReadableDatabase();
        Cursor cursor = db2.query(CoreContract.CoreEntry.tableName, null, null, null, null, null, null);
        Toast.makeText(this.getContext(), "Stored "+cursor.getCount()+" contacts in cache", Toast.LENGTH_SHORT).show();
    }
}
