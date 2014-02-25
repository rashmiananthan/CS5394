package com.example.whatscookin;

import android.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 

public class SignUp extends Activity {
	
	Button btnRegister;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
    private ProgressDialog pDialog;
    
 // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String register_tag = "register";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    public String error_message;
    public int flag = 0;
 // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json, json_user;
	
    private static final String REGISTER_URL = "http://whatscookinadmin.dukealums.com/loginregister.php";
    private static final String TAG = "SignUpActivity";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		// Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
              	
        btnRegister.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
            	new LoadRegister().execute();
            }
          });
        }
        
        class LoadRegister extends AsyncTask<String, String, String> {
        	boolean failure = false;
        	 @Override
        	 protected void onPreExecute() {
        		 super.onPreExecute();
        		 pDialog = new ProgressDialog(SignUp.this);
        		 pDialog.setMessage("Attempting to register...");
        		 pDialog.setIndeterminate(false);
        		 pDialog.setCancelable(true);
        		 pDialog.show();

        	 }
        	 
        	 @Override

             protected String doInBackground(String... args) {
                  
        		 String name = inputFullName.getText().toString();
                 String email = inputEmail.getText().toString();
                 String password = inputPassword.getText().toString();
                 
                 // Building Parameters
                 List<NameValuePair> params = new ArrayList<NameValuePair>();
                 params.add(new BasicNameValuePair("tag", register_tag));
                 params.add(new BasicNameValuePair("name", name));
                 params.add(new BasicNameValuePair("email", email));
                 params.add(new BasicNameValuePair("password", password));
                 json = jsonParser.makeHttpRequest(REGISTER_URL, "POST",params);
                 Log.d(TAG, "inside doInBackground");
                 try {
                            if((json.getInt(KEY_SUCCESS)) == 4){
                            	 Log.d(TAG, "SignUp Success");
                               	 
                                 // user successfully logged in
                                 // Store user details in SQLite Database
                               	  DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                               	  json_user = json.getJSONObject("user");
                                  // Clear all previous data in database
                                  db.logoutUser(getApplicationContext());
                                  db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL));
                                  flag = 1;
                                   
                             }else{
                            	// String err = json.getString(KEY_ERROR);
                            	 Log.d(TAG, "Error during SignUp");
                            	// if((Integer.parseInt(err) == 1)||(Integer.parseInt(err) == 2)){
                            		 error_message = json.getString(KEY_ERROR_MSG);
                            		 Log.d(TAG, error_message);
                            		 flag = 2;
                            	 //}
                             }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }	
                 
                 return null;
				}
        	 
                    
             /**

              * After completing background task Dismiss the progress dialog

              * **/

             @SuppressWarnings("deprecation")
			protected void onPostExecute(String file_url) {
                 // dismiss the dialog once product deleted
                 pDialog.dismiss();
                 AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();
                 if(flag == 1){
                	 flag = 0;
                	 Log.d(TAG, "starting login.class");
                	 
                	// Setting Dialog Title
                     alertDialog.setTitle("SignUp Successful");
                  // Setting Dialog Message
                     alertDialog.setMessage("SignUp successful, you will now be redirected to Home page");
                  // Setting OK Button
                     alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                             // Write your code here to execute after dialog closed
                            	 Intent dashboard = new Intent(getApplicationContext(), HomePage.class);
                            	 startActivity(dashboard); 
                             }
                     });
              
                     // Showing Alert Message
                     alertDialog.show();
                     // Close Login Screen
                    // finish();
                 }else if(flag == 2){
                	 flag = 0;
                	 Log.d(TAG, "starting homepage.class");
                	 
                	// Setting Dialog Title
                     alertDialog.setTitle("SignUp Error");
                  // Setting Dialog Message
                     alertDialog.setMessage(error_message);
                  // Setting OK Button
                     alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                             // Write your code here to execute after dialog closed
                            	 Intent dashboard = new Intent(getApplicationContext(), SignUp.class);
                            	 startActivity(dashboard); 
                             }
                     });
                     
                     // Showing Alert Message
                     alertDialog.show();
                   //  finish();
                 }
              
                 /*if (file_url != null){
                     Toast.makeText(LogIn.this, file_url, Toast.LENGTH_LONG).show();
                 }*/
             }

           } 
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

}
