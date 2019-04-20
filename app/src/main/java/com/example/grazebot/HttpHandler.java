package com.example.grazebot;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

class HttpHandler {

    private static final String TAG = "HttpHandler";
    private final OnResponseReceived callback;

    interface OnResponseReceived {
        void onResponseReceived(String response);
    }

    // Constructor
    HttpHandler(OnResponseReceived callback){
        this.callback = callback;
    }

    void makeRequest(String reqUrl, JSONObject data){
        new ClientRequest().execute(reqUrl, data.toString());
    }

    private class HttpService{

        HttpService(){

        }

        // Request to Server
        private String makeServiceCall(String reqUrl, String data) {
            String response = null;
            try {
                // Create URL from IP address and port passed
                URL url = new URL(reqUrl);

                // Create a HttpUrlConnection to interact with the server
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Using Post as method of communication from client to server
                conn.setRequestMethod("POST");
                // Create a DataOutputStream to send bytes of data to the server
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                // Send a json string
                dos.writeBytes(data);
                dos.close();
                // Create an input stream to read from for the server response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                // Build the response from the input stream
                response = convertStreamToString(in);
            } catch(MalformedURLException e){
                Log.e(TAG, "makeServiceCall: " + e.getMessage());
            } catch(ProtocolException e){
                Log.e(TAG, "makeServiceCall: " + e.getMessage());
            } catch(IOException e) {
                Log.e(TAG, "makeServiceCall: " + e.getMessage());
            } catch(Exception e) {
                Log.e(TAG, "makeServiceCall: " + e.getMessage() );
            }
            return response;
        }

        // Helper method to build a response from the many lines in a server response
        private String convertStreamToString(InputStream is) {
            // Setup I/O resources
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            // Append the incoming lines to the string builder and with until the null/<EOF> is found
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                    Log.e(TAG, "convertStreamToString: " + line );
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }



    // AsyncTask to allow communication with Python Server
    @SuppressLint("StaticFieldLeak")
    private class ClientRequest extends AsyncTask<String, Void, String> {
        // uns on GUI thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Runs on separate thread, File I/O, Networking, etc. done here.
        @Override
        protected String doInBackground(String... strings) {
            HttpService sh = new HttpService();
            // Create URL to connect to (i.e. Python server on local network)
            String url = strings[0];
            String data = strings[1];
            Log.e(TAG, "doInBackground: " + url + "  Data: " + data);
            // Obtain the JSON response from the server
            String jsonStr = sh.makeServiceCall(url, data);

            // Show what was the response from the Python server
            Log.e(TAG, "Response from url: " + jsonStr);
            return jsonStr;
        }

        // Runs on GUI thread
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            callback.onResponseReceived(response);
            Log.e(TAG, "onPostExecute: " + response );
        }
    }

}

