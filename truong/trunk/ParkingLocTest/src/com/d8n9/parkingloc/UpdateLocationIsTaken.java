package com.d8n9.parkingloc;

import com.google.android.gms.maps.CameraUpdateFactory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UpdateLocationIsTaken extends Activity {
	
	TextView descriptionInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location);
		
		Bundle b = getIntent().getExtras();
		Integer id = b.getInt("id");
		String name = b.getString("name");
		final Double currLat = b.getDouble("currLat");
		final Double currLng = b.getDouble("currLng");
		final Double destLat = b.getDouble("destLat");
		final Double destLng = b.getDouble("destLng");
		Log.d("ID ", id.toString());
		Log.d("Name: ", name); 
		Log.d("CurrentLocation: ", "(" + currLat + ", " + currLng + ")");
		Log.d("Destination: ", "(" + destLat + " " + destLng + ")");
		
		
		descriptionInfo = (TextView)findViewById(R.id.descInfo);
		descriptionInfo.setText(name);
		
		findViewById(R.id.navigateButton).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
            		Uri.parse("http://maps.google.com/maps?saddr=" + currLat + "," + currLng + "&daddr=" + destLat + "," + destLng));
        		startActivity(intent);
            }
        });
		
		findViewById(R.id.cancelToMap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
}
