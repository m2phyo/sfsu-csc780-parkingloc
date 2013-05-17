package com.d8n9.parkingloc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class MapActivity 
			extends Activity 
			implements OnCameraChangeListener, OnMapClickListener, OnMapLongClickListener, LocationSource, LocationListener, OnMarkerDragListener, OnInfoWindowClickListener{
	
	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	
	Location myLocation;
	TextView tvLocInfo;
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_LOCATIONS = "location_info";
	private static final String TAG_TASK = "get";
	private static final String TAG_LID = "loc_id";
	private static final String TAG_NAME = "loc_name";
	private static final String TAG_LATITUDE = "loc_latitude";
	private static final String TAG_LONGITUDE = "loc_longitude";
	
	// url to get all products list
	private static String url_all_products = "http://thecity.sfsu.edu/~m2phyo/getLocation.php";
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> locationsList;
	
	// products JSONArray
	JSONArray locations = null;
	
	List<Integer> availableSpotsId = new ArrayList<Integer>();
	List<String> availableSpotsName = new ArrayList<String>();
	List<LatLng> availableSpots = new ArrayList<LatLng>();
	
	// Current location and destination lat/lng
	double currLat;
	double currLng;
	double destLat;
	double destLng;
	
	// Bundle for transfering data between activities
	Bundle b = new Bundle();
	
	Double homeLat;
	Double homeLng;
	
	boolean homeMarkerAdded = false;
	boolean currentLocationMarkerAdded = false;
	
	LocationManager myLocationManager = null;
	OnLocationChangedListener myLocationListener = null;
	Criteria myCriteria;
	
	static final LatLng USA = new LatLng(37.090240, -95.712891);
	static final LatLng currentLocation = new LatLng(37.723886, -122.477067);
	LatLng home;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		// Small text view on top of the map
		tvLocInfo = (TextView)findViewById(R.id.tv_location);
		
		// Loading map fragments
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment 
			= (MapFragment)myFragmentManager.findFragmentById(R.id.map);
		myMap = myMapFragment.getMap();
		
		myMap.setMyLocationEnabled(true);
		
		// Setting for on map control buttons
		myMap.getUiSettings().setMyLocationButtonEnabled(false);
		myMap.getUiSettings().setZoomControlsEnabled(true);
		
		// Select map type
		myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		// myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		
		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
		// Move the camera to USA and zoom to 3 within 2500 ms
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(USA, 3), 2500, null);	

		// Detect single tap on the map
		myMap.setOnMapClickListener(this);
		
		// Detect long tap on the map
		myMap.setOnMapLongClickListener(this);
		
		// Detect drag on marker
		myMap.setOnMarkerDragListener(this);
		
		// Detect camera screen change
		myMap.setOnCameraChangeListener(this);
		
		// Detect tap on marker's info window
		myMap.setOnInfoWindowClickListener(this);
		
		// Get info from intent bundle
		Bundle b = getIntent().getExtras();

		homeLat = b.getDouble("home_lat");
		homeLng = b.getDouble("home_lng");
		home = new LatLng(homeLat, homeLng);
		
		// Home button on click
		findViewById(R.id.home_button).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	if (!homeMarkerAdded) {
            		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home, 14), 3000, addHomeMarkerCallback);
            	} else {
            		myMap.animateCamera(CameraUpdateFactory.newLatLng(home), 1500, null);
            	}
            }
        });
		
		// Current location button on click
		findViewById(R.id.current_location).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	if (!currentLocationMarkerAdded) {
            		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14), 3000, addCurrentLocationMarkerCallback);
            	} else {
            		myMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation), 1500, null);
            	}
            }
        });
		
		// Refresh button on click
		findViewById(R.id.refresh_button).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	myMap.clear();
            	availableSpots.clear();
            	availableSpotsId.clear();
            	availableSpotsName.clear();
            	
            	//Calling background AsyncTask to get all the Locations from Database
            	new LoadAllLocations().execute();
            	
            	
            }
        });
	}

	@Override
	protected void onResume() {
		super.onResume();

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		
		if (resultCode == ConnectionResult.SUCCESS){
			myLocationManager.requestLocationUpdates(
				0L,    //minTime
				0.0f,    //minDistance
				myCriteria,  //criteria
				this,    //listener
				null);   //looper
			
			// Replaces the location source of the my-location layer.
			myMap.setLocationSource(this);
		}else{
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		}
	}

	@Override
	protected void onPause() {
		myMap.setLocationSource(null);
		myLocationManager.removeUpdates(this);

		super.onPause();
	}
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		myLocationListener = listener;
	}

	@Override
	public void deactivate() {
		myLocationListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (myLocationListener != null) {
			myLocationListener.onLocationChanged(location);
			
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			
			tvLocInfo.setText(	"lat: " + lat + "\n" +
								"lon: " + lon);
			
			LatLng latlng= new LatLng(location.getLatitude(), location.getLongitude());
			myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	CancelableCallback addHomeMarkerCallback =
		new CancelableCallback(){
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onFinish() {
				if (!homeMarkerAdded) {
            		myMap.addMarker(new MarkerOptions().position(home).snippet("home").title("Home").icon(BitmapDescriptorFactory
            	            .fromResource(R.drawable.home2)));
            		homeMarkerAdded = true;
            	}
			}
		};
		
	CancelableCallback addCurrentLocationMarkerCallback =
		new CancelableCallback(){
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onFinish() {
				if (!currentLocationMarkerAdded) {
            		myMap.addMarker(new MarkerOptions().position(currentLocation).snippet("current location").title("Current Location").icon(BitmapDescriptorFactory
            	            .fromResource(R.drawable.current_location)));
            		currentLocationMarkerAdded = true;
            	}
			}
		};

	// Action on map click
	@Override
	public void onMapClick(LatLng point) {
		myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
	}
	
	// Action on map click and hold (long click)
	@Override
	public void onMapLongClick(LatLng point) {
		myMap.addMarker(new MarkerOptions().position(point).snippet("Click to store this spot").title("New Spot"));
	}

	// Action on dragging marker
	@Override
	public void onMarkerDrag(Marker marker) {
		
	}

	// Action on ending dragging marker
	@Override
	public void onMarkerDragEnd(Marker marker) {
		
	}

	// Action on starting dragging marker
	@Override
	public void onMarkerDragStart(Marker marker) {
		
	}

	// Action after camera view change on the map
	@Override
	public void onCameraChange(CameraPosition position) {
		tvLocInfo.setText("CameraPosition: " + position);
	}

	// Action on click on marker's info window
	@Override
	public void onInfoWindowClick(Marker marker) {
		currLat = currentLocation.latitude;			// current location
		currLng = currentLocation.longitude;
		destLat = marker.getPosition().latitude;		// destination
		destLng = marker.getPosition().longitude;
		
		// Clear and put variables into bundle
		b.clear();
		b.putString("name", marker.getTitle());	// Marker Name
		b.putDouble("currLat", currLat);	// currentLocation latitude
		b.putDouble("currLng", currLng);	// currentLocation longitude
		b.putDouble("destLat", destLat);	// destination latitude
		b.putDouble("destLng", destLng);	// destination longitude
		String markerTitle = marker.getTitle();
		
		// If this is a new spot
		if ("New Spot".equals(markerTitle)) {
			Intent intent = new Intent(MapActivity.this, UpdateLocationToDb.class);
			intent.putExtras(b);	// put bundle into intent
			startActivity(intent);
			
		// If this is home
		} else if ("Home".equals(markerTitle)) {
			Toast.makeText(getApplicationContext(), "This is your home", Toast.LENGTH_SHORT).show();
			
		// If this is current location
		} else if ("Current Location".equals(markerTitle)) {
			Toast.makeText(getApplicationContext(), "This is your current location", Toast.LENGTH_SHORT).show();
			
		// Otherwise, these are markers from the database
		} else {
			b.putInt("id", Integer.parseInt(marker.getSnippet()));	// Marker Id
			Intent intent = new Intent(MapActivity.this, UpdateLocationIsTaken.class);
			intent.putExtras(b);	// put bundle into intent
			startActivity(intent);
		}
	}
	
	/**
	 * Background Async Task to Load all locations by making HTTP Request
	 * */
	class LoadAllLocations extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MapActivity.this);
			pDialog.setMessage("Loading locations from database. Please wait...");
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
			
			params.add(new BasicNameValuePair("tag", TAG_TASK));
			params.add(new BasicNameValuePair("loc_isTaken", "0"));
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Locations: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// Location found
					// Getting Array of Location
					locations = json.getJSONArray(TAG_LOCATIONS);

					// looping through All Products
					for (int i = 0; i < locations.length(); i++) {
						JSONObject c = locations.getJSONObject(i);

						// Storing each json item in variable
						Integer id = c.getInt(TAG_LID);
						String name = c.getString(TAG_NAME);
						Double latitude = c.getDouble(TAG_LATITUDE);
						Double longitude = c.getDouble(TAG_LONGITUDE);
						
						availableSpotsId.add(id);
						availableSpotsName.add(name);
						availableSpots.add(new LatLng(latitude, longitude));
					}
				} else {
					// No location found
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
			LatLngBounds bounds = myMap.getProjection().getVisibleRegion().latLngBounds;

            boolean check = false;
            if (currentLocationMarkerAdded) {
            	myMap.addMarker(new MarkerOptions().position(currentLocation).snippet("Your current location").title("Current Location").icon(BitmapDescriptorFactory
        	            .fromResource(R.drawable.current_location)));
            }
            if (homeMarkerAdded) {
            	myMap.addMarker(new MarkerOptions().position(home).snippet("This is your home").title("Home").icon(BitmapDescriptorFactory
        	            .fromResource(R.drawable.home2)));
            }
    		for (int i=0; i<availableSpots.size(); i++) {
    			check = bounds.contains(availableSpots.get(i));
    			if (check) {
    				myMap.addMarker(new MarkerOptions().position(availableSpots.get(i)).snippet(availableSpotsId.get(i).toString()).title(availableSpotsName.get(i)));
    			}
    		}
		}
	}	
}
