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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateLocationIsTaken extends Activity {

	TextView descriptionInfo;
	
	EditText inputName;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	TextView loginErrorMsg;      // TextView to show the result of MySQL query 
	String returnString="";   // to store the result of MySQL query after decoding JSON
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	Bundle b;
	
	Double locLat;
	Double locLong;
	Integer id;
	Integer user_id;
	
	//JSON Response keys
	private static String KEY_SUCCESS = "success";
	private static String KEY_TAG = "check";
	
	// Web service address
	private static String webURL = "http://thecity.sfsu.edu/~m2phyo/getLocation.php";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location_is_taken);
		
		b = getIntent().getExtras();
		
		id = b.getInt("id");
		user_id = b.getInt("user_id");
		String name = b.getString("name");
		final Double currLat = b.getDouble("currLat");	// Current Location
		final Double currLng = b.getDouble("currLng");
		final Double destLat = b.getDouble("destLat");	// Destination location
		final Double destLng = b.getDouble("destLng");
		Log.d("ID ", id.toString());
		Log.d("Name: ", name); 
		Log.d("CurrentLocation: ", "(" + currLat + ", " + currLng + ")");
		Log.d("Destination: ", "(" + destLat + " " + destLng + ")");
		
		locLat = destLat;
		locLong = destLng;
		
		descriptionInfo = (TextView)findViewById(R.id.descInfo);
		loginErrorMsg = (TextView)findViewById(R.id.locinfo_error);
		descriptionInfo.setText(name);
		
		// Reserve button
		findViewById(R.id.reserveButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new updateIsTakenProcess().execute();
            }
        });
		
		// Navigate button
		findViewById(R.id.navigateButton).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	// Calling Google Maps
            	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
            		Uri.parse("http://maps.google.com/maps?saddr=" + currLat + "," + currLng + "&daddr=" + destLat + "," + destLng));
        		startActivity(intent);
            }
        });
		
		// Back button
		findViewById(R.id.cancelToMap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
	
    // Mark spot as taken in database
    class updateIsTakenProcess extends AsyncTask<String, String, String> {
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UpdateLocationIsTaken.this);
			pDialog.setMessage("Updating... Please wait... ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
        }
        
        @Override
        protected String doInBackground(String... postParameters) {
    
            // call executeHttpPost method passing necessary parameters 
            try {
	            // declare parameters that are passed to PHP script
	            List<NameValuePair> postParam = new ArrayList<NameValuePair>();
	            
	            postParam.add(new BasicNameValuePair("tag", KEY_TAG));
	            postParam.add(new BasicNameValuePair("loc_id",id.toString()));
	            postParam.add(new BasicNameValuePair("user_id", user_id.toString()));
	            
	            JSONObject response = jParser.makeHttpRequest(webURL, "POST", postParam);
	 
	            // store the result returned by PHP script that runs MySQL query
	            String result = response.toString();  
	            Log.d("isTaken :", result);
	        
	            try {
	            	// Checking for SUCCESS TAG isTaken Retrieve
	                int success = response.getInt(KEY_SUCCESS);
	 
	                if (success == 1) {
	                	returnString = "\n" + response.getString("message");
	                } else {
	        		// no products found
	                returnString = "\n" + response.getString("message");
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
				Log.e("log_tag","Error: " + e.toString());;          
			}
		}
	}
}
