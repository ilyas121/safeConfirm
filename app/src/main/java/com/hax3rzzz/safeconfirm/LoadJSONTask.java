package com.hax3rzzz.safeconfirm;

/**
 * Created by MILESWT on 7/22/2017.
 */

import android.os.AsyncTask;

import com.hax3rzzz.safeconfirm.model.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadJSONTask extends AsyncTask<String, Void, JSONObject> {

    public LoadJSONTask(Listener listener) {

        mListener = listener;
    }

    public interface Listener {
        void onLoaded(JSONObject androidList);

        void onError();
    }

    private Listener mListener;

    //    @Override
    protected JSONObject doInBackground(String... strings) {
        try {

            String stringResponse = loadJSON(strings[0]);
//            Log.d("myTag", "JSON: " + stringResponse);
            JSONObject jsonthing = new JSONObject(stringResponse);
            return jsonthing;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject response) {

        if (response != null) {

            try {
                JSONObject weather = response;//.getJSONObject("weather");
                mListener.onLoaded(weather);
//            } catch (JSONException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            mListener.onError();
        }
    }

    private String loadJSON(String jsonURL) throws IOException {

        URL url = new URL(jsonURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line.trim());//added trim()
        }

        in.close();
        return response.toString();
    }
}