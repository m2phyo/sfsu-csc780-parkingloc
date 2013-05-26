package com.d8n9.parkingloc;

import com.d8n9.parkingloc.JSONParser;

import com.d8n9.parkingloc.R;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {
	Button logButton;
	EditText inputEmail;
	EditText inputPassword;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	
	// Bundle for transfering data between activities
	Bundle b = new Bundle();
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	TextView loginErrorMsg;      // TextView to show the result of MySQL query 
	
	// Web service address
	private static String webURL = "http://thecity.sfsu.edu/~m2phyo/login.php";
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	//JSON Response keys
	private static String KEY_SUCCESS = "success";
	private static String KEY_TAG = "login";
	// products JSONArray
    JSONArray user = null;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        //Setting Variables for input texts and buttons
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        logButton = (Button) findViewById(R.id.btnLogin);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        
        // Login button
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	attemptLogin();
            }
        });

        // Register button
        findViewById(R.id.btnLinkToRegisterScreen).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
	}
    
    /**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
    public void attemptLogin() {

		// Reset errors.
		inputEmail.setError(null);
		inputPassword.setError(null);

		// Store values at the time of the login attempt.
		mEmail = inputEmail.getText().toString();
		mPassword = inputPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			inputPassword.setError(getString(R.string.error_field_required));
			focusView = inputPassword;
			cancel = true;
		} else if (mPassword.length() < 4) {
			inputPassword.setError(getString(R.string.error_invalid_password));
			focusView = inputPassword;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			inputEmail.setError(getString(R.string.error_field_required));
			focusView = inputEmail;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			inputEmail.setError(getString(R.string.error_invalid_email));
			focusView = inputEmail;
			cancel = true;
		} else if (!mEmail.contains(".")) {
			inputEmail.setError(getString(R.string.error_invalid_email));
			focusView = inputEmail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Start login process
			new loginProcess().execute();
		}
	}
    
    /**
     * Login in background using Async Task
     * */
    class loginProcess extends AsyncTask<String, String, String> {
    	
        // Pre execute
        @Override
        protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Login... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
        }
        
        // Execute in background
        protected String doInBackground(String... postParameters) {
            String name = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
    
            String returnString="";	// To store the result of MySQL query after decoding JSON
            
            // Call executeHttpPost method passing necessary parameters 
            try {
	            // Declare parameters that are passed to PHP script
	            List<NameValuePair> postParam = new ArrayList<NameValuePair>();
	            postParam.add(new BasicNameValuePair("tag", KEY_TAG));
	            postParam.add(new BasicNameValuePair("user_name",name));
	            postParam.add(new BasicNameValuePair("password",password));
	            
	            // Post method
	            JSONObject response = jParser.makeHttpRequest(webURL, "POST", postParam);
	            
	            // Store the result returned by PHP script that runs MySQL query
	            String result = response.toString();  
	            Log.d("Login Result: ", result);
	            
	            try {
	            	// Checking for SUCCESS TAG
	                int success = response.getInt(KEY_SUCCESS);
	 
	                if (success == 1) {
	                	// Clear the bundle and add data
	                	b.clear();
	                	b.putInt("user_id", response.getJSONArray("user").getJSONObject(0).getInt("user_id"));
	                	b.putString("username", response.getJSONArray("user").getJSONObject(0).getString("user_name"));
	                	b.putString("password", response.getJSONArray("user").getJSONObject(0).getString("password"));
	                	b.putDouble("home_lat", response.getJSONArray("user").getJSONObject(0).getDouble("home_loc_lat"));
	                	b.putDouble("home_lng", response.getJSONArray("user").getJSONObject(0).getDouble("home_loc_lng"));
	                	b.putString("home_add", response.getJSONArray("user").getJSONObject(0).getString("home_loc_add"));
	                	b.putInt("reserved_id", response.getJSONArray("user").getJSONObject(0).getInt("reserved_id"));
	                	
	            		Intent home = new Intent(getApplicationContext(), HomeActivity.class);
	            		home.putExtras(b);
	                    // Close all views before launching Home
		                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                startActivity(home);
	                } else {
	        		// no products found
	                returnString = "\n" + response.getString("message");
	                }
	            } catch (JSONException e) {
	            	e.printStackTrace();
	            }          
        	} catch (Exception e) {
                Log.e("log_tag","Error in http connection!!" + e.toString());     
        	}
            return returnString;
        }

		// Post execute
		protected void onPostExecute(String returnText) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			try {
				loginErrorMsg.setText(returnText);
			} catch(Exception e){
				Log.e("log_tag","Error: " + e.toString());;          
			}
		}
	}
}