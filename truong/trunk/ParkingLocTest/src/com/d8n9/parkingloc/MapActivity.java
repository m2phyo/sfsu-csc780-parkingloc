package com.d8n9.parkingloc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
 
public class MapActivity extends Activity 
			implements OnMapClickListener, OnMapLongClickListener, LocationSource, LocationListener, OnMarkerDragListener{
	
	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	
	Location myLocation;
	TextView tvLocInfo;
	
	ArrayList<LatLng> listPoint = new ArrayList<LatLng>();
	boolean homeMarkerAdded = false;
	boolean currentLocationMarkerAdded = false;
	
	LocationManager myLocationManager = null;
	OnLocationChangedListener myLocationListener = null;
	Criteria myCriteria;
	
	static final LatLng USA = new LatLng(37.090240, -95.712891);
	static final LatLng currentLocation = new LatLng(37.723886, -122.477067);
	static final LatLng home = new LatLng(37.782426, -122.416223);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
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
            		myMap.addMarker(new MarkerOptions().position(home).title("Home").icon(BitmapDescriptorFactory
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
	            		myMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location").icon(BitmapDescriptorFactory
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
		myMap.addMarker(new MarkerOptions().position(point).title(point.toString()).draggable(true));
		listPoint.add(point);
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}
}
