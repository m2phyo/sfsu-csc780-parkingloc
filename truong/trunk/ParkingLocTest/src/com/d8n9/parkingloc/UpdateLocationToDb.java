package com.d8n9.parkingloc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UpdateLocationToDb extends Activity {
	
	TextView descriptionInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location_to_db);
		
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
		descriptionInfo.setText("Latitude: " + destLat + "\nLongitude: " + destLng);
		
		// Add button
		findViewById(R.id.addLoc).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TO DO
            }
        });
		
		// Back button
		findViewById(R.id.cancelToMap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
}
