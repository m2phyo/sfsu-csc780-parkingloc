package com.d8n9.parkingloc;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class ChangePassActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pass);
		
		findViewById(R.id.change_pass_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
}
