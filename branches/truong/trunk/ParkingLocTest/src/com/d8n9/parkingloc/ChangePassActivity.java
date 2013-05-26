package com.d8n9.parkingloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePassActivity extends Activity {
	
    EditText inputOldPassword;
    EditText inputNewPassword;
    TextView changePasswordErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_TAG = "changePassword";
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pass);
		
	    inputOldPassword = (EditText) findViewById(R.id.oldPassword);
	    inputNewPassword = (EditText) findViewById(R.id.newPassword);
	    changePasswordErrorMsg = (TextView) findViewById(R.id.change_password_error);
		
		
		findViewById(R.id.change_pass).setOnClickListener(new OnClickListener() {
			public void onClick(View view){
				new changePasswordProcess().execute();
			}
		});
		
		findViewById(R.id.change_pass_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
	}
	
    /**
     * Background Async Task
     * */
    class changePasswordProcess extends AsyncTask<String, String, String> {
        
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
                        super.onPreExecute();
                        pDialog = new ProgressDialog(ChangePassActivity.this);
                        pDialog.setMessage("Updating Password.... Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();
        }
        
        /**
         * getting result from url
         * */
        @Override
        protected String doInBackground(String... postParameters) {
            Bundle b = getIntent().getExtras();
        	
        	String oldPassword = inputOldPassword.getText().toString();
            String newPassword = inputNewPassword.getText().toString();
            String userName = b.getString("username");
            
            String returnString = "";
            // call executeHttpPost method passing necessary parameters 
            try {
                    // declare parameters that are passed to PHP script
                    List<NameValuePair> postParam = new ArrayList<NameValuePair>();
                    
                    postParam.add(new BasicNameValuePair("tag", KEY_TAG));
                    postParam.add(new BasicNameValuePair("user_name",userName));
                    postParam.add(new BasicNameValuePair("old_password",oldPassword));
                    postParam.add(new BasicNameValuePair("new_password",newPassword));
                    Log.d("user and password: ",postParam.toString());
                    //String response = null;
                    
                    JSONObject changePasswordResponse = jParser.makeHttpRequest(
                                "http://thecity.sfsu.edu/~m2phyo/changePassword.php", //remote server
                                "POST", postParam);  // POST method
         
                    // store the result returned by PHP script that runs MySQL query
                    String result = changePasswordResponse.toString();  
                    Log.d("Change Password User :", result);
         
                    returnString += "\n" + changePasswordResponse.getString("message");
                        
                         
                } catch (Exception e) {
                Log.e("log_tag","Error in http connection!!" + e.toString());     
                }
            return returnString;
        }
        
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String changePasswordText) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            try {
                    changePasswordErrorMsg.setText(changePasswordText);
            } catch(Exception e){
                    Log.e("log_tag","Error in Display!" + e.toString());;          
            }
        }
    }
	
	
}
