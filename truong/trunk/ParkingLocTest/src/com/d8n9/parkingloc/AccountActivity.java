package com.d8n9.parkingloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
 
public class AccountActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        
        
        findViewById(R.id.change_password).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Bundle b = getIntent().getExtras();
                Intent changePass = new Intent(getApplicationContext(), ChangePassActivity.class);
                changePass.putExtras(b);
                startActivity(changePass);
                finish();
            	
            	// NO ACTION FOR NOW
            }
        });
        
        findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}