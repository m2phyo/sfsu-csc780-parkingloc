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

import com.d8n9.parkingloc.locationinfo.LocListActivity;
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
	List<LatLng> listPoint = new ArrayList<LatLng>();
	List<String> listPointId = new ArrayList<String>();
	List<LatLng> availableSpots = new ArrayList<LatLng>();
	List<LatLng> currentZoneOld = new ArrayList<LatLng>();
	List<LatLng> currentZoneNew = new ArrayList<LatLng>();
	
	boolean homeMarkerAdded = false;
	boolean currentLocationMarkerAdded = false;
	int markerCounter = 0;
	
	LocationManager myLocationManager = null;
	OnLocationChangedListener myLocationListener = null;
	Criteria myCriteria;
	
	static final LatLng USA = new LatLng(37.090240, -95.712891);
	static final LatLng currentLocation = new LatLng(37.723886, -122.477067);
	static final LatLng home = new LatLng(37.782426, -122.416223);
	
//	LatLng spot1 = new LatLng(37.783130, -122.418509);
//	LatLng spot2 = new LatLng(37.781247, -122.418981);
//	LatLng spot3 = new LatLng(37.778737, -122.418187);
//	LatLng spot4 = new LatLng(37.780976, -122.414002);
//	LatLng spot5 = new LatLng(37.778144, -122.427092);
//	LatLng spot6 = new LatLng(37.789981, -122.424259);
//	
//	LatLng spot7 = new LatLng(37.722358, -122.473569);
//	LatLng spot8 = new LatLng(37.719999, -122.473826);
//	LatLng spot9 = new LatLng(37.726058, -122.477324);
//	LatLng spot10 = new LatLng(37.730335, -122.486358);
//	LatLng spot11 = new LatLng(37.719914, -122.483268);
//	LatLng spot12 = new LatLng(37.717504, -122.479405);
	
	LatLng tempLL = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		
//		availableSpots.add(spot1);
//		availableSpots.add(spot2);
//		availableSpots.add(spot3);
//		availableSpots.add(spot4);
//		availableSpots.add(spot5);
//		availableSpots.add(spot6);
//		availableSpots.add(spot7);
//		availableSpots.add(spot8);
//		availableSpots.add(spot9);
//		availableSpots.add(spot10);
//		availableSpots.add(spot11);
//		availableSpots.add(spot12);
		
		
		tvLocInfo = (TextView)findViewById(R.id.tv_location);
		
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment 
			= (MapFragment)myFragmentManager.findFragmentById(R.id.map);
		myMap = myMapFragment.getMap();
		
		myMap.setMyLocationEnabled(true);
		myMap.getUiSettings().setMyLocationButtonEnabled(false);
		myMap.getUiSettings().setZoomControlsEnabled(true);
		
		myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		//myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		
		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
		// Move the camera to USA and zoom to 3 within 2500 ms
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(USA, 3), 2500, null);	

		// Detect single tap on the map
		myMap.setOnMapClickListener(this);
		
		// Detect long tap on the map
		myMap.setOnMapLongClickListener(this);
		
		// 
		myMap.setOnMarkerDragListener(this);
		
		//
		myMap.setOnCameraChangeListener(this);
		
		//
		myMap.setOnInfoWindowClickListener(this);
		
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
//		
//		Geocoder g = new Geocoder(this, Locale.getDefault());
//		try {
//			List<Address> addresses = g.getFromLocationName("empire state building", 1);
//			if (addresses.size() > 0) {
//				Address singleAdd = addresses.get(0);
//				if (singleAdd.hasLatitude() && singleAdd.hasLongitude()) {
//					double selectedLat = singleAdd.getLatitude();
//		            double selectedLng = singleAdd.getLongitude();
//		            LatLng place = new LatLng(selectedLat, selectedLng);
//		            myMap.addMarker(new MarkerOptions().position(place).title("Here is the road location")
//		            			.snippet("Hon the lads"));
//				}
//			}
//		} catch (IOException e) {
//            e.printStackTrace();
//        }
		
		
		// Geocoder: translate an address to longitude and latitude
//		Geocoder g = new Geocoder(this);
//	    List<Address> addressList = new ArrayList<Address>();
//	    String streetName = "San Francisco State University";
//	    try {
//	        addressList = g.getFromLocationName(streetName, 1);
//
//	    } catch (IOException e) {
//	        Toast.makeText(this, "Location not found",     Toast.LENGTH_SHORT)
//	                    .show();
//	            e.printStackTrace();
//
//	    } finally {
//	    	if (addressList.get(0) != null) {
//		        Address address = addressList.get(0);
//	
//		        if (address.hasLatitude() && address.hasLongitude()) {
//		            double selectedLat = address.getLatitude();
//		            double selectedLng = address.getLongitude();
//		            LatLng place = new LatLng(selectedLat, selectedLng);
//		            myMap.addMarker(new MarkerOptions().position(place).title("Here is the road location")
//		            			.snippet("Hon the lads"));
//		        }
//	    	}
//	    }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		
		if (resultCode == ConnectionResult.SUCCESS){
//			Toast.makeText(getApplicationContext(), 
//					"isGooglePlayServicesAvailable SUCCESS", 
//					Toast.LENGTH_SHORT).show();
			
			// Register for location updates using a Criteria, and a callback on the specified looper thread.
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
			
			tvLocInfo.setText(
					"lat: " + lat + "\n" +
					"lon: " + lon);
			
			LatLng latlng= new LatLng(location.getLatitude(), location.getLongitude());
			myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onMapClick(LatLng point) {
		tvLocInfo.setText(point.toString());
		myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
	}
	
	@Override
	public void onMapLongClick(LatLng point) {
		tvLocInfo.setText("New marker added @ " + point.toString());
		myMap.addMarker(new MarkerOptions().position(point).snippet("Click to store this spot").title("New Spot"));
		listPoint.add(point);
		markerCounter++;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
//		for (int i=0; i<listPoint.size(); i++) {
//			if (listPoint.get(i) == tempLL) {
//				marker.setPosition(marker.getPosition());
//				marker.setTitle(marker.getPosition().toString());
//				listPoint.add(i, marker);
//			}
//		}
		tvLocInfo.setText("Marker ID: " + marker.getId());
//		tvLocInfo.setText("Marker ID: " + marker.getId() + "\n" + listPoint.get(0) + "\n" + listPoint.get(1) + "\n" + listPoint.get(2));
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
//		tempLL = marker.getPosition();
		
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		// TODO Auto-generated method stub
		tvLocInfo.setText("CameraPosition: " + position);
		
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
//		Toast.makeText(getApplicationContext(), marker.getSnippet(), Toast.LENGTH_SHORT).show();
		
		double currLat = currentLocation.latitude;			// current location
		double currLng = currentLocation.longitude;
		double destLat = marker.getPosition().latitude;		// destination
		double destLng = marker.getPosition().longitude;
		
		// Creating a bundle for next activity
		Bundle b = new Bundle();
		
		b.putString("name", marker.getTitle());					// Marker Name
		b.putDouble("currLat", currLat);	// currentLocation latitude
		b.putDouble("currLng", currLng);	// currentLocation longitude
		b.putDouble("destLat", destLat);	// destination latitude
		b.putDouble("destLng", destLng);	// destination longitude
		
		if ("New Spot".equals(marker.getTitle())) {
			Intent intent = new Intent(MapActivity.this, UpdateLocationToDb.class);
			intent.putExtras(b);	// put bundle into intent
			startActivity(intent);
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
					// products found
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
			LatLngBounds bounds = myMap.getProjection().getVisibleRegion().latLngBounds;

            boolean check = false;
            if (currentLocationMarkerAdded) {
            	myMap.addMarker(new MarkerOptions().position(currentLocation).snippet("Your current location").title("Current Location").icon(BitmapDescriptorFactory
        	            .fromResource(R.drawable.current_location)));
//            	currentLocationMarkerAdded = false;
            }
            if (homeMarkerAdded) {
            	myMap.addMarker(new MarkerOptions().position(home).snippet("This is your home").title("Home").icon(BitmapDescriptorFactory
        	            .fromResource(R.drawable.home2)));
//            	homeMarkerAdded = false;
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
