package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateLocationToDb extends Activity {
	
	TextView descriptionInfo;
	
	EditText inputName;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	TextView loginErrorMsg;      // TextView to show the result of MySQL query 
	String returnString="";   // to store the result of MySQL query after decoding JSON
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	Double locLat;
	Double locLong;
	
	//JSON Response keys
	private static String KEY_SUCCESS = "success";
	private static String KEY_TAG = "insert";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location_to_db);
		
		Bundle b = getIntent().getExtras();

		final Double destLat = b.getDouble("destLat");
		final Double destLng = b.getDouble("destLng");
		Log.d("Destination: ", "(" + destLat + " " + destLng + ")");
		
		locLat = destLat;
		locLong = destLng;
		
		descriptionInfo = (TextView)findViewById(R.id.descInfo);
		loginErrorMsg = (TextView)findViewById(R.id.locinfo_error);
		descriptionInfo.setText("Latitude: " + destLat + "\nLongitude: " + destLng);
		inputName = (EditText) findViewById(R.id.locName);
		
		// Add button
		findViewById(R.id.addLoc).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new addLocationProcess().execute();
            }
        });
		
		// Back button
		findViewById(R.id.cancelToMap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
	
    /**
     * Background Async Task
     * */
    class addLocationProcess extends AsyncTask<String, String, String> {
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UpdateLocationToDb.this);
			pDialog.setMessage("Adding... Please wait... ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
        }
        
        /**
         * getting result from url
         * */
        @Override
        protected String doInBackground(String... postParameters) {
            String name = inputName.getText().toString();
            
            Log.d("locLat: ", locLat.toString());
            Log.d("locLong: ", locLong.toString());
    
            // call executeHttpPost method passing necessary parameters 
            try {
	            // declare parameters that are passed to PHP script
	            List<NameValuePair> postParam = new ArrayList<NameValuePair>();
	            
	            
	            postParam.add(new BasicNameValuePair("tag", KEY_TAG));
	            postParam.add(new BasicNameValuePair("loc_name",name));
	            postParam.add(new BasicNameValuePair("loc_latitude",locLat.toString()));
	            postParam.add(new BasicNameValuePair("loc_longitude",locLong.toString()));
	            
	            //String response = null;
	            
	            JSONObject response = jParser.makeHttpRequest(
	            		"http://thecity.sfsu.edu/~m2phyo/getLocation.php",	//remote server
	            		"POST", postParam);								// POST method
	 
	            // store the result returned by PHP script that runs MySQL query
	            String result = response.toString();  
	            Log.d("Location :", result);
	        
	            try {
	            	// Checking for SUCCESS TAG
	                int success = response.getInt(KEY_SUCCESS);
	 
	                if (success == 1) {
	                	returnString += "\n" + response.getString("message");
	                } else {
	        		// no products found
	                returnString += "\n" + response.getString("message");
	                }
	            } catch (JSONException e) {
	            	e.printStackTrace();
	            }          
        	} catch (Exception e) {
                Log.e("log_tag","Error in http connection!!" + e.toString());     
        	}
            return returnString;
        }

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
        @Override
		protected void onPostExecute(String returnText) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			try {
				loginErrorMsg.setText(returnText);
			} catch(Exception e){
				Log.e("log_tag","Error in Display!" + e.toString());;          
			}
		}
	}
	
}
