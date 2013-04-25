package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import com.d8n9.parkingloc.JSONParser;
import com.d8n9.parkingloc.library.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_TAG = "register";
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
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
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
 
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	new registerProcess().execute();
//                String name = inputFullName.getText().toString();
//                String email = inputEmail.getText().toString();
//                String password = inputPassword.getText().toString();
//                UserFunctions userFunction = new UserFunctions();
//                JSONObject json = userFunction.registerUser(name, email, password);
// 
//                // check for login response
//                try {
//                    if (json.getString(KEY_SUCCESS) != null) {
//                        registerErrorMsg.setText("");
//                        String res = json.getString(KEY_SUCCESS);
//                        if(Integer.parseInt(res) == 1){
//                            // user successfully registred
//                            // Store user details in SQLite Database
//                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
//                            JSONObject json_user = json.getJSONObject("user");
// 
//                            // Clear all previous data in database
//                            userFunction.logoutUser(getApplicationContext());
//                            db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
//                            // Launch Dashboard Screen
//                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
//                            // Close all views before launching Dashboard
//                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(dashboard);
//                            // Close Registration Screen
//                            finish();
//                        }else{
//                            // Error in registration
//                            registerErrorMsg.setText("Error occured in registration");
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
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
     * Background Async Task
     * */
    class registerProcess extends AsyncTask<String, String, String> {
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Register.... Please wait...");
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
    
            String returnString = "";
            // call executeHttpPost method passing necessary parameters 
            try {
	            // declare parameters that are passed to PHP script
	            List<NameValuePair> postParam = new ArrayList<NameValuePair>();
	            
	            postParam.add(new BasicNameValuePair("tag", KEY_TAG));
	            postParam.add(new BasicNameValuePair("user_name",name));
	            postParam.add(new BasicNameValuePair("password",password));
	            Log.d("password: ",password);
	            //String response = null;
	            
	            JSONObject response = jParser.makeHttpRequest(
	            		"http://thecity.sfsu.edu/~m2phyo/register.php",	//remote server
	            		"POST", postParam);								// POST method
	 
	            // store the result returned by PHP script that runs MySQL query
	            String result = response.toString();  
	            Log.d("Register User :", result);
	        
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