package genbob.de.surveather;

import android.os.AsyncTask;
import android.util.Log;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by genbob on 22.04.16.
 */
public class ApiClient extends AsyncTask<URL, Integer , JsonNode[]>{
    private ObjectMapper mapper;

    public static final String TAG = "APICLIENT";

    public ApiClient(){
        this.mapper = new ObjectMapper();


    }
    //make a api call with given URLs
    @Override
    protected JsonNode[] doInBackground(URL... params) {
            JsonNode[] out = new JsonNode[params.length];

            Log.i(TAG, "URL: " + params[0].toString());
            for (int i = 0; i<params.length; i++){
                try{
                HttpURLConnection con = (HttpURLConnection) params[i].openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("GET");
                con.connect();
                if(con.getResponseCode() == 200){
                    InputStream in = con.getInputStream();
                    JsonNode root = this.mapper.readTree(in);
                    Log.i(TAG, root.toString());
                    out[i] = root;
                }
            }catch(IOException e){
                    Log.e(TAG,"Server request connection"+ e.getStackTrace());

                }
        }
        return out;
    }
    @Override
    protected void onPostExecute(JsonNode[] node){
        super.onPostExecute(node);
    }





}
