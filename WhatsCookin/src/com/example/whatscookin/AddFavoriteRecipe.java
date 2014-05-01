package com.example.whatscookin;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AddFavoriteRecipe extends Activity{
	
	private static final String url_add_favorite = "http://whatscookinadmin.dukealums.com/add_Favorite.php";
	// JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = "Add Favorite";
    private static final String TAG_ID = "id";
	int flag = 0;
    private String id;
    DatabaseHandler db;
    SessionManagement session;
    private String email;
    HashMap<String,String> user;
	// Progress Dialog
    private ProgressDialog pDialog;
    
    TextView txtFavMsg;
    
 
    
    
    JSONParser jsonParser = new JSONParser();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_recipe);
        
        Intent i = getIntent();
		 
        // getting product id (id) from intent
        id = i.getStringExtra(TAG_ID);
        db = new DatabaseHandler(this);
        session = new SessionManagement(this);
       
        user = db.getUserDetails();
		email = user.get("email");
        txtFavMsg = (TextView)findViewById(R.id.inputFavRecipe);
        new AddFavorite().execute();
        
        
        // View Favorites button
       
    }
    
    /**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_recipe, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    
    class AddFavorite extends AsyncTask<String,String,String>{
    	
    	
    	/**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddFavoriteRecipe.this);
            pDialog.setMessage("Loading recipe details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			if(email==null){
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
				email = db.getUserDetails().get("email");
			}
	        params.add(new BasicNameValuePair("email",email));
	        params.add(new BasicNameValuePair("recId", id));
	        
	        JSONParser jsonParser = new JSONParser();
	        JSONObject json = jsonParser.makeHttpRequest(url_add_favorite,"POST", params);
	        
	        try {
	        	int success = json.getInt(TAG_SUCCESS);
				                
                if (success == 1) {
                	flag = 1;
               }else{
            	   flag = 2;
               }
				
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        
	        
	        
			return null;
		}
		
		
		  /* After completing background task Dismiss the progress dialog
		     * **/
		 @SuppressWarnings("deprecation")
			protected void onPostExecute(String file_url) {
              // dismiss the dialog once product deleted
              pDialog.dismiss();
              AlertDialog alertDialog = new AlertDialog.Builder(AddFavoriteRecipe.this).create();
              if(flag == 1){
             	 flag = 0;
             	 alertDialog.setTitle("Success");
                 // Setting Dialog Message
                    alertDialog.setMessage("Successfully added your recipe to your favorites");
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
                  
              }else if(flag == 2){
             	 flag = 0;
             	 Log.d(TAG, "starting signup.class");
             	 
             	 //loginErrorMsg.setText(error_message);
             	// Setting Dialog Title
                  alertDialog.setTitle("Error");
               // Setting Dialog Message
                  alertDialog.setMessage("The recipe is already present in your favorites.");
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
                 // finish();
              }
              if (file_url != null){
                  Toast.makeText(AddFavoriteRecipe.this, file_url, Toast.LENGTH_LONG).show();
              }
          }

        } 

 
		
		
    	
    }


