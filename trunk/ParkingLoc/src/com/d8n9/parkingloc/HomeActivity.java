package com.d8n9.parkingloc;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

// Main activity after Login that contains 3 tabs: Map, Reserved, Account.
@SuppressWarnings("deprecation")
public class HomeActivity extends TabActivity {
	Bundle b;
	Integer reserved_id;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        // Get data passed by previous activity
        b = getIntent().getExtras();
        reserved_id = b.getInt("reserved_id");
        
        // LogCat
        Log.d("Reserved ID in HomeActivity: ", reserved_id.toString());
        
        TabHost tabHost = getTabHost();
         
        // Tab for Map
        TabSpec mapTab = tabHost.newTabSpec("Map");
        // setting Title and Icon for the Tab
        mapTab.setIndicator("Map", getResources().getDrawable(R.drawable.map_tab));
        Intent mapIntent = new Intent(this, MapActivity.class);
        // Add data to an intent for passing to another activity
        mapIntent.putExtras(b);
        mapTab.setContent(mapIntent);
         
        // Tab for Reserved
        TabSpec reserveTab = tabHost.newTabSpec("Reserved");        
        reserveTab.setIndicator("Reserved", getResources().getDrawable(R.drawable.list_tab));
        Intent reserveIntent = new Intent(this, ReservedActivity.class);
        // Add data to an intent for passing to another activity
        reserveIntent.putExtras(b);
        reserveTab.setContent(reserveIntent);
         
        // Tab for Account
        TabSpec accountTab = tabHost.newTabSpec("Account");
        accountTab.setIndicator("Account", getResources().getDrawable(R.drawable.account_tab));
        Intent accountIntent = new Intent(this, AccountActivity.class);
        // Add data to an intent for passing to another activity
        accountIntent.putExtras(b);
        accountTab.setContent(accountIntent);
         
        // Adding all TabSpec to TabHost
        tabHost.addTab(mapTab);		// Adding map tab
        tabHost.addTab(reserveTab);	// Adding list tab
        tabHost.addTab(accountTab);	// Adding account tab
    }
}