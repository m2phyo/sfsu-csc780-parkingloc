package com.d8n9.parkingloc.locationinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.d8n9.parkingloc.JSONParser;
import com.d8n9.parkingloc.R;
import com.d8n9.parkingloc.R.id;
import com.d8n9.parkingloc.R.layout;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class LocListActivity extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> locationsList;

	// url to get all products list
	private static String url_all_products = "http://thecity.sfsu.edu/~m2phyo/getLocation.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_LOCATIONS = "location_info";
	private static final String TAG_TASK = "get";
	private static final String TAG_LID = "loc_id";
	private static final String TAG_NAME = "loc_name";
	private static final String TAG_LATITUDE = "loc_latitude";
	private static final String TAG_LONGITUDE = "loc_longitude";

	// products JSONArray
	JSONArray locations = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_locations);

		// Hashmap for ListView
		locationsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllLocations().execute();

		// Get listview
//		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// getting values from selected ListItem
//				String pid = ((TextView) view.findViewById(R.id.pid)).getText()
//						.toString();
//
//				// Starting new intent
//				Intent in = new Intent(getApplicationContext(),
//						EditProductActivity.class);
//				// sending pid to next activity
//				in.putExtra(TAG_PID, pid);
//				
//				// starting new activity and expecting some response back
//				startActivityForResult(in, 100);
//			}
//		});

	}

	// Response from Edit Product Activity
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		// if result code 100
//		if (resultCode == 100) {
//			// if result code 100 is received 
//			// means user edited/deleted product
//			// reload this screen again
//			Intent intent = getIntent();
//			finish();
//			startActivity(intent);
//		}
//
//	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllLocations extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LocListActivity.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("tag", TAG_TASK));
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Locations: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Location
					locations = json.getJSONArray(TAG_LOCATIONS);

					// looping through All Products
					for (int i = 0; i < locations.length(); i++) {
						JSONObject c = locations.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_LID);
						String name = c.getString(TAG_NAME);
						String latitude = c.getString(TAG_LATITUDE);
						String longitude = c.getString(TAG_LONGITUDE);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_LID, id);
						map.put(TAG_NAME, name);
						map.put(TAG_LATITUDE, latitude);
						map.put(TAG_LONGITUDE, longitude);

						// adding HashList to ArrayList
						locationsList.add(map);
					}
				} else {
//					// no products found
//					// Launch Add New product Activity
//					Intent i = new Intent(getApplicationContext(),
//							NewProductActivity.class);
//					// Closing all previous activities
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							LocListActivity.this, locationsList,
							R.layout.list, new String[] { TAG_LID,
									TAG_NAME},
							new int[] { R.id.lid, R.id.lname });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}
	
	}
}