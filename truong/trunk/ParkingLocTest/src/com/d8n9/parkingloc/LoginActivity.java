package com.d8n9.parkingloc;

import com.d8n9.parkingloc.JSONParser;

import com.d8n9.parkingloc.R;
import com.d8n9.parkingloc.R.id;
import com.d8n9.parkingloc.R.layout;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
	
	// Keep track of the login task to ensure we can cancel it if requested.
	private loginProcess mAuthTask = null;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	TextView loginErrorMsg;      // TextView to show the result of MySQL query 
	String returnString="";   // to store the result of MySQL query after decoding JSON
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	//JSON Response keys
	private static String KEY_SUCCESS = "success";
	private static String KEY_MESSAGE = "message";
	private static String KEY_NAME = "user_name";
	private static String KEY_TAG = "login";
	private static String KEY_USER = "user";
	private static String KEY_ID = "user_id";

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
        
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	attemptLogin();
            }
        });

        // Link to Register Screen
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
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
//			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//			showProgress(true);
//			mAuthTask = new UserLoginTask();
//			mAuthTask.execute((Void) null);
			new loginProcess().execute();
		}
	}
    
    /**
     * Background Async Task
     * */
    class loginProcess extends AsyncTask<String, String, String> {
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Login... Please wait... ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
        }
        
        /**
         * getting result from url
         * */
        protected String doInBackground(String... postParameters) {
            String name = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
    
            // call executeHttpPost method passing necessary parameters 
            try {
	            // declare parameters that are passed to PHP script
	            List<NameValuePair> postParam = new ArrayList<NameValuePair>();
	            
	            postParam.add(new BasicNameValuePair("tag", KEY_TAG));
	            postParam.add(new BasicNameValuePair("user_name",name));
	            postParam.add(new BasicNameValuePair("password",password));
	            //String response = null;
	            
	            JSONObject response = jParser.makeHttpRequest(
	            		"http://thecity.sfsu.edu/~m2phyo/login.php",	//remote server
	            		"POST", postParam);								// POST method
	 
	            // store the result returned by PHP script that runs MySQL query
	            String result = response.toString();  
	            Log.d("User :", result);
	        
	            try {
	            	// Checking for SUCCESS TAG
	                int success = response.getInt(KEY_SUCCESS);
	 
	                if (success == 1) {
	    				Intent home = new Intent(getApplicationContext(), HomeActivity.class);
	                    // Close all views before launching Home
		                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                startActivity(home);
	                } else {
	        		// no products found
	                returnString += "\n" + response.getString("message");
	                }
	            } catch (JSONException e) {
	            	e.printStackTrace();
	            }          
        	} catch (Exception e) {
                Log.e("log_tag","Error in http connection!!" + e.toString());     
        	}
            return null;
        }

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			try {
				loginErrorMsg.setText(returnString);
			} catch(Exception e){
				Log.e("log_tag","Error in Display!" + e.toString());;          
			}
		}
	}
}