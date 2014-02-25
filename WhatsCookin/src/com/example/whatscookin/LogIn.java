package com.example.whatscookin;

import android.app.AlertDialog;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Build;
import android.app.ProgressDialog;


public class LogIn extends Activity{

	Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
    private ProgressDialog pDialog;
    
 // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String login_tag = "login";
    public String error_message;
    public int flag = 0;
    private static final String LOGIN_URL = "http://whatscookinadmin.dukealums.com/loginregister.php";
    private static final String TAG = "LogInActivity";
    
 // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json;
	
   	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		// Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        
     // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
        public void onClick(View view) {
        	new LoadLogin().execute();
        }
        });
	}	
	
        
        class LoadLogin extends AsyncTask<String, String, String> {
        	boolean failure = false;
        	 @Override
        	 protected void onPreExecute() {
        		 super.onPreExecute();
        		 pDialog = new ProgressDialog(LogIn.this);
        		 pDialog.setMessage("Attempting login...");
        		 pDialog.setIndeterminate(false);
        		 pDialog.setCancelable(true);
        		 pDialog.show();

        	 }
        	 
        	 @Override

             protected String doInBackground(String... args) {
           		     String email = inputEmail.getText().toString();
                     String password = inputPassword.getText().toString();
                     // Building Parameters
                     List<NameValuePair> params = new ArrayList<NameValuePair>();
                     params.add(new BasicNameValuePair("tag", login_tag));
                     params.add(new BasicNameValuePair("email", email));
                     params.add(new BasicNameValuePair("password", password));
                     json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",params);
                     Log.d(TAG, json.toString());
                     Log.d(TAG, "inside doInBackground");
                     try {
                    	 	 if((json.getInt(KEY_SUCCESS))==1){
                        	 Log.d(TAG, "LogIn successful");
                        	 
                             // user successfully logged in
                             // Store user details in SQLite Database
                             DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                             JSONObject json_user = json.getJSONObject("user");
                             // Clear all previous data in database
                             db.logoutUser(getApplicationContext());
                             db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL)); 
                             flag = 1;
                             
                    	 	 }else{
                    	 		 Log.d(TAG, json.getString(KEY_ERROR_MSG));
                    	 		 flag = 2;
                    	     }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }	
                 
                 return null;
				}
        	 
             /* After completing background task Dismiss the progress dialog */

             @SuppressWarnings("deprecation")
			protected void onPostExecute(String file_url) {
                 // dismiss the dialog once product deleted
                 pDialog.dismiss();
                 AlertDialog alertDialog = new AlertDialog.Builder(LogIn.this).create();
                 if(flag == 1){
                	 flag = 0;
                	 Log.d(TAG, "starting homepage.class");
                	 
                	 Intent dashboard = new Intent(getApplicationContext(), HomePage.class);
                     // Close all views before launching Dashboard
                     dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     
                     startActivity(dashboard);
                     finish();
                 }else if(flag == 2){
                	 flag = 0;
                	 Log.d(TAG, "starting signup.class");
                	 
                	 //loginErrorMsg.setText(error_message);
                	// Setting Dialog Title
                     alertDialog.setTitle("Login Error");
                  // Setting Dialog Message
                     alertDialog.setMessage("Please login again with proper credentials");
                  // Setting OK Button
                     alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                             // Write your code here to execute after dialog closed
                            	 Intent dashboard = new Intent(getApplicationContext(), LogIn.class);
                            	startActivity(dashboard); 
                             }
                     });
              
                     // Showing Alert Message
                     alertDialog.show();
                    // finish();
                 }
                 if (file_url != null){
                     Toast.makeText(LogIn.this, file_url, Toast.LENGTH_LONG).show();
                 }
             }

           } 

    	@Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    		// Inflate the menu; this adds items to the action bar if it is present.
    		getMenuInflater().inflate(R.menu.sign_up, menu);
    		return true;
    	}
        
}  

        
   
	

