package gokulan.cfi.com.contacts;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gokulan on 4/18/17.
 */

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    public static final String REQ_METHOD = "GET";
    public static final int REQ_TIMEOUT = 15000;
    public static final int CONN_TIMEOUT = 15000;
    private ProgressBar pb;
    private ExecuteDoneCallback exb;

    public HttpGetRequest(ProgressBar mpb, ExecuteDoneCallback ex){
        pb = mpb;
        exb = ex;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String line;
        StringBuilder builder = new StringBuilder();
        String result = new String();

        try {
            URL murl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) murl.openConnection();

            conn.setRequestMethod(REQ_METHOD);
            conn.setReadTimeout(REQ_TIMEOUT);
            conn.setConnectTimeout(CONN_TIMEOUT);

            conn.connect();

            InputStreamReader iReader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(iReader);

            while((line = reader.readLine())!=null)
            {
                builder.append(line+"\n");
            }

            reader.close();
            iReader.close();

            result = builder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        pb.setVisibility(View.GONE);
        exb.executeDoneCallback(result);
        return;
    }
}
