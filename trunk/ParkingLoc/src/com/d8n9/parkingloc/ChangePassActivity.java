package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePassActivity extends Activity {
	
    EditText inputOldPassword;
    EditText inputNewPassword;
    TextView changePasswordErrorMsg;
 
    // JSON Response node names
    private static String KEY_TAG = "changePassword";
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Web service address
	private static String webURL = "http://thecity.sfsu.edu/~m2phyo/changePassword.php";
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pass);
		
		// Text fields
	    inputOldPassword = (EditText) findViewById(R.id.oldPassword);
	    inputNewPassword = (EditText) findViewById(R.id.newPassword);
	    changePasswordErrorMsg = (TextView) findViewById(R.id.change_password_error);
		
		// Change Password button
		findViewById(R.id.change_pass).setOnClickListener(new OnClickListener() {
			public void onClick(View view){
				new changePasswordProcess().execute();
			}
		});
		
		// Back button
		findViewById(R.id.change_pass_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
	
    /**
     * Change password in background using Async Task
     * */
    class changePasswordProcess extends AsyncTask<String, String, String> {
        
        // Pre execute
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChangePassActivity.this);
            pDialog.setMessage("Updating Password... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        
        // Execute in background
        @Override
        protected String doInBackground(String... postParameters) {
        	// Get data passed by previous activity
            Bundle b = getIntent().getExtras();
        	String oldPassword = inputOldPassword.getText().toString();
            String newPassword = inputNewPassword.getText().toString();
            String userName = b.getString("username");
            
            String returnString = "";
            
            // Call executeHttpPost method passing necessary parameters 
            try {
                // Declare parameters that are passed to PHP script
                List<NameValuePair> postParam = new ArrayList<NameValuePair>();
                
                // Adding data before Posting
                postParam.add(new BasicNameValuePair("tag", KEY_TAG));
                postParam.add(new BasicNameValuePair("user_name",userName));
                postParam.add(new BasicNameValuePair("old_password",oldPassword));
                postParam.add(new BasicNameValuePair("new_password",newPassword));
                Log.d("User and Pass: ",postParam.toString());
                
                // Post method
                JSONObject changePasswordResponse = jParser.makeHttpRequest(webURL, "POST", postParam);
     
                // Store the result returned by PHP script that runs MySQL query
                String result = changePasswordResponse.toString();
                Log.d("Change Pass Result:", result);
     
                returnString = "\n" + changePasswordResponse.getString("message");
                
            } catch (Exception e) {
            	Log.e("log_tag","Error in http connection!!" + e.toString());     
            }
            return returnString;
        }
        
        // Post execute
        protected void onPostExecute(String returnString) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            try {
                changePasswordErrorMsg.setText(returnString);
            } catch(Exception e){
                Log.e("log_tag","Error: " + e.toString());;          
            }
        }
    }
}
