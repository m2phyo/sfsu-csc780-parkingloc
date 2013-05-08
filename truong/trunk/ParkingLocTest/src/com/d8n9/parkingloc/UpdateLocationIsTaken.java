package com.d8n9.parkingloc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class UpdateLocationIsTaken extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location);
		
		Bundle b = getIntent().getExtras();
		Integer id = b.getInt("id");
		String name = b.getString("name");
		Log.d("ID ", id.toString());
		Log.d("Name: ", name); 
	}

}
