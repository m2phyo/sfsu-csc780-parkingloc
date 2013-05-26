package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class RegisterActivity extends Activity {
	Button btnRegister;
    Button btnLinkToLogin;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordRepeat;
    EditText inputHomeAddress;
    TextView registerErrorMsg;
    
    // Values for email and password at the time of the login attempt.
 	private String mEmail;
 	private String mPassword;
 	private String mPasswordRepeat;
 	private String mHomeAddress;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_TAG = "register";
        
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Web service address
	private static String webURL = "http://thecity.sfsu.edu/~m2phyo/register1.php";
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
        
	    // Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		inputPasswordRepeat = (EditText) findViewById(R.id.registerPasswordRepeat);
		inputHomeAddress = (EditText) findViewById(R.id.homeAddress);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
	
	    // Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    	attemptRegister();
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
    
    /**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
    public void attemptRegister() {

		// Reset errors.
		inputEmail.setError(null);
		inputPassword.setError(null);

		// Store values at the time of the login attempt.
		mEmail = inputEmail.getText().toString();
		mPassword = inputPassword.getText().toString();
		mPasswordRepeat = inputPasswordRepeat.getText().toString();
		mHomeAddress = inputHomeAddress.getText().toString();

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
		} else {
			// Check for a valid repeated password.
			if (TextUtils.isEmpty(mPasswordRepeat)) {
				inputPasswordRepeat.setError(getString(R.string.error_field_required));
				focusView = inputPasswordRepeat;
				cancel = true;
			} else if (mPasswordRepeat.length() < 4) {
				inputPasswordRepeat.setError(getString(R.string.error_invalid_password));
				focusView = inputPasswordRepeat;
				cancel = true;
			} else if (!mPassword.equals(mPasswordRepeat)) {
				inputPasswordRepeat.setError(getString(R.string.error_matching_password));
				focusView = inputPasswordRepeat;
				cancel = true;
			}
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
			new registerProcess().execute();
		}
	}
    
    // Register in the background using Async Task
    class registerProcess extends AsyncTask<String, String, String> {
        
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Registering... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        
        /**
         * getting result from url
         * */
        @Override
        protected String doInBackground(String... postParameters) {
            String name = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String homeAddress = inputHomeAddress.getText().toString();
    
            String returnString = "";
            // call executeHttpPost method passing necessary parameters 
            try {
                // declare parameters that are passed to PHP script
                List<NameValuePair> postParam = new ArrayList<NameValuePair>();
                
                postParam.add(new BasicNameValuePair("tag", KEY_TAG));
                postParam.add(new BasicNameValuePair("user_name",name));
                postParam.add(new BasicNameValuePair("password",password));
                postParam.add(new BasicNameValuePair("home_address", homeAddress));
                Log.d("password: ",password);
                //String response = null;
                
                // Post method
                JSONObject response = jParser.makeHttpRequest(webURL, "POST", postParam);
     
                // store the result returned by PHP script that runs MySQL query
                String result = response.toString();  
                Log.d("Register User :", result);
            
                try {
                    // Checking for SUCCESS TAG
                    int success = response.getInt(KEY_SUCCESS);
     
                    if (success == 1) {
                        returnString = "\n" + response.getString("message");
                    } else {
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
        
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String resultText) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            try {
                registerErrorMsg.setText(resultText);
            } catch(Exception e){
                Log.e("log_tag","Error in Display!" + e.toString());;          
            }
        }
    }
}