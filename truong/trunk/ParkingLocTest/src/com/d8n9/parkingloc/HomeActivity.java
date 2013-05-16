package com.d8n9.parkingloc;

import com.d8n9.parkingloc.locationinfo.*;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class HomeActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
         
        Bundle b = getIntent().getExtras();
        
        TabHost tabHost = getTabHost();
         
        // Tab for Map
        TabSpec mapspec = tabHost.newTabSpec("Map");
        // setting Title and Icon for the Tab
        mapspec.setIndicator("Map", getResources().getDrawable(R.drawable.map_tab));
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtras(b);
        mapspec.setContent(mapIntent);
         
        // Tab for List
        TabSpec listspec = tabHost.newTabSpec("List");        
        listspec.setIndicator("List", getResources().getDrawable(R.drawable.list_tab));
        Intent listIntent = new Intent(this, LocListActivity.class);
        listspec.setContent(listIntent);
         
        // Tab for Account
        TabSpec accountspec = tabHost.newTabSpec("Account");
        accountspec.setIndicator("Account", getResources().getDrawable(R.drawable.account_tab));
        Intent accountIntent = new Intent(this, AccountActivity.class);
        accountspec.setContent(accountIntent);
         
        // Adding all TabSpec to TabHost
        tabHost.addTab(mapspec);		// Adding map tab
        tabHost.addTab(listspec);		// Adding list tab
        tabHost.addTab(accountspec);	// Adding account tab
    }
}