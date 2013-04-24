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
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
	EditText inputName;
	EditText inputPassword;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	TextView tv;      // TextView to show the result of MySQL query 
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
	
	//url to get login autorization.
	private static String url_login = "http://thecity.sfsu.edu/login.php";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        //Setting Variables for input texts and buttons
        inputName = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        logButton = (Button) findViewById(R.id.sign_in_button);
        //tv = (TextView) findViewById(R.id.showresult);
        
        logButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new loginProcess().execute();
			}
			
		});
        
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
			pDialog.setMessage("Login.... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
    	
        /**
         * getting result from url
         * */
        protected String doInBackground(String... postParameters) {
        	
				String name = inputName.getText().toString();
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
		    		 "http://thecity.sfsu.edu/~m2phyo/login.php", //remote server
		    		 //"http://thecity.sfsu.edu/~m2phyo/index.php", //remote server
		    		 "POST", postParam);
		     
		        	// store the result returned by PHP script that runs MySQL query
		        	String result = response.toString();  
		            Log.d("User :", result);
		            
			        try {
		                // Checking for SUCCESS TAG
		                int success = response.getInt(KEY_SUCCESS);
		 
		                if (success == 1) {
		                    // products found
		                    // Getting Array of Products
		                    user = response.getJSONArray(KEY_USER);
		 
		                    // looping through All users
		                    //for (int i = 0; i < user.length(); i++) {
		                        JSONObject c = user.getJSONObject(0);
		 
		                        // Storing each json user item in variable
		                        String id = c.getString(KEY_ID);
		                        String user_name = c.getString(KEY_NAME);
		 
		                        returnString += "\n" + id + "->" + user_name;		                        
		                        
		                        // creating new HashMap
		                        HashMap<String, String> map = new HashMap<String, String>();
		 
		                        // adding each child node to HashMap key => value
		                        map.put(KEY_ID, id);
		                        map.put(KEY_NAME, user_name);
		 
		                        // adding HashList to ArrayList
		                        //user.add(map);
		                    }
		                //}

		                else {
//		                    // no products found
		                	returnString += "\n" + response.getString("message");
//		                    // Launch Add New product Activity
//		                    Intent i = new Intent(getApplicationContext(),
//		                            NewProductActivity.class);
//		                    // Closing all previous activities
//		                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		                    startActivity(i);
		                }
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		        
		        	        
		        }catch (Exception e) {
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
	        try{
	            tv.setText(returnString);
	           }
	           catch(Exception e){
	            Log.e("log_tag","Error in Display!" + e.toString());;          
	           } 
		}
	}
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.login, menu);
//        return true;
//    }
    
}
