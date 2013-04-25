package com.d8n9.parkingloc;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements LocationListener, LocationSource {
	
	final int RQS_GooglePlayServices = 1;
	private GoogleMap mMap;

	private OnLocationChangedListener mListener;
	private LocationManager locationManager;
	
	private String homeAddress = "421 Turk St, San Francisco, CA 94102";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	    if(locationManager != null)
	    {
	        boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        if(gpsIsEnabled)
	        {
	            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
	        }
	        else if(networkIsEnabled)
	        {
	            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
	        }
	        else
	        {
	            //Show an error dialog that GPS is disabled.
	        }
	    }
	    else
	    {
	        //Show a generic error dialog since LocationManager is null for some reason
	    }

	    setUpMapIfNeeded();
	}
	
	@Override
	public void onPause()
	{
	    if(locationManager != null)
	    {
	        locationManager.removeUpdates(this);
	    }

	    super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setUpMapIfNeeded();
		
		if(locationManager != null)
	    {
	        mMap.setMyLocationEnabled(true);
	    }

//		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//		
//		if (resultCode == ConnectionResult.SUCCESS){
//			Toast.makeText(getApplicationContext(), 
//					"isGooglePlayServicesAvailable SUCCESS", 
//					Toast.LENGTH_SHORT).show();
//		}else{
//			GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
//		}
		
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView
	 * MapView}) will show a prompt for the user to install/update the Google Play services APK on
	 * their device.
	 * <p>
	 * A user can return to this Activity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the Activity may not have been
	 * completely destroyed during this process (it is likely that it would only be stopped or
	 * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
	 * {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) 
	    {
	        // Try to obtain the map from the SupportMapFragment.
	        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        // Check if we were successful in obtaining the map.

	        if (mMap != null) 
	        {
	            setUpMap();
	        }

	        // This is how you register the LocationSource
	        mMap.setLocationSource(this);
	    }
	}
	
	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() 
	{
	    mMap.setMyLocationEnabled(true);
	}

	
	@Override
	public void onLocationChanged(Location location) {
//		if( mListener != null )
//        {
//            mListener.onLocationChanged( location );
// 
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//        }
		
//		if( mListener != null )
//	    {
//	        mListener.onLocationChanged( location );
//	 
//	        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
//	 
//	        if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
//	        {
//	             //Move the camera to the user's location once it's available!
//	             mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//	        }
//	    }
		
		changeMapLocation(location);
	}
	
	private void changeMapLocation(Location location) {
        LatLng latlong = new LatLng(location.getLatitude(),
                location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(this, "Provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Toast.makeText(this, "Provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Toast.makeText(this, "Status changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

}
