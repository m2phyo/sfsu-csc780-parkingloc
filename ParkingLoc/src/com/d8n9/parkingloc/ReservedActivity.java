package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ReservedActivity extends Activity {
	
	TextView ReservedMsg;
	ProgressDialog pDialog;
	
	Bundle b;
	
	String returnString;
	Integer user_id;
	Integer reserved_id; 
	
	// Web service address
	private static String webURL = "http://thecity.sfsu.edu/~m2phyo/getLocation.php";
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserved);
        b = getIntent().getExtras();
        user_id = b.getInt("user_id");
        reserved_id = b.getInt("reserved_id");
        
        ReservedMsg = (TextView)findViewById(R.id.reservedMsg);
        
        findViewById(R.id.releaseButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ReleaseSpot().execute();
            }
        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new GetReserveInfo().execute();
	}
	
	// Getting reservation info
	class GetReserveInfo extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ReservedActivity.this);
			pDialog.setMessage("Loading... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All locations from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("tag", "getReserved"));
			params.add(new BasicNameValuePair("user_id", user_id.toString()));
			Log.d("Reserved ID: ", reserved_id.toString());
			params.add(new BasicNameValuePair("loc_id", reserved_id.toString()));
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(webURL, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("Reserved Spot: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					returnString = json.getString("message");
				} else {
					returnString = json.getString("message");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return returnString;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String resultText) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			ReservedMsg.setText(resultText);
		}
	}
	
	// Releasing a spot in the background using Async Task
	class ReleaseSpot extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ReservedActivity.this);
			pDialog.setMessage("Releasing... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All locations from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("tag", "release"));
			params.add(new BasicNameValuePair("user_id", user_id.toString()));
			params.add(new BasicNameValuePair("loc_id", reserved_id.toString()));
			Log.d("Reserved ID to release: ", reserved_id.toString());
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(webURL, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("Release Spot: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					returnString = json.getString("message");
				} else {
					returnString = json.getString("message");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return returnString;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String resultText) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			ReservedMsg.setText(resultText);
		}
	}
}
