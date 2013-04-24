package com.d8n9.parkingloc;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MapActivity extends Activity {
  static final LatLng SFSU = new LatLng(37.721102, -122.476788);
  static final LatLng SPOT1 = new LatLng(37.726548, -122.473327);
  static final LatLng SPOT2 = new LatLng(37.728324, -122.474053);
  static final LatLng SPOT3 = new LatLng(37.739497, -122.487265);
  static final LatLng SPOT4 = new LatLng(37.715987, -122.477815);
//  static final LatLng KIEL = new LatLng(53.551, 9.993);
  private GoogleMap map;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    Marker sfsu = map.addMarker(new MarkerOptions().position(SFSU).title("SFSU"));
    Marker spot1 = map.addMarker(new MarkerOptions().position(SPOT1).title("SPOT1"));
    Marker spot2 = map.addMarker(new MarkerOptions().position(SPOT2).title("SPOT2"));
    Marker spot3 = map.addMarker(new MarkerOptions().position(SPOT3).title("SPOT3"));
    Marker spot4 = map.addMarker(new MarkerOptions().position(SPOT4).title("SPOT4"));
    

//    Marker kiel = map.addMarker(new MarkerOptions()
//        .position(KIEL)
//        .title("Kiel")
//        .snippet("Kiel is cool")
//        .icon(BitmapDescriptorFactory
//            .fromResource(R.drawable.ic_launcher)));

    // Move the camera instantly to SFSU with a zoom of 10.
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(SFSU, 10));

    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
}