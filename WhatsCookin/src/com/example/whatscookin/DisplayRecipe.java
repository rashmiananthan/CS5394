package com.example.whatscookin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;












import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;


import android.widget.Button;
import android.widget.TextView;




public class DisplayRecipe extends Activity {
	
	String id;
	String name;
	String instructions;
	//int calories;
	int time;
	
	
	TextView txtName;
	TextView txtInstructions;
	TextView txtCalories;
	TextView txtTime;
	TextView txtScore;
	TextView txtingredients;
	DatabaseHandler db;
    boolean userloggedin;
    SessionManagement session; 	 
    // Progress Dialog
    private ProgressDialog pDialog;
    Button btnAddFavorite;
    // JSON parser class
    JSONParser jParser = new JSONParser();
    JSONObject recipesM=null;
    

 
    // single product url
    private static final String url_display_recipe = "http://whatscookinadmin.dukealums.com/display_recipe.php";
 
    
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECIPES = "recipes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TIME = "time";
    private static final String TAG_CALORIES = "calories";
   // private static final String TAG_SCORE = "score";
    private static final String TAG_INSTRUCTIONS = "instructions";
    private static final String TAG_INGREDIENTS = "ingredients";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_recipe);
		Intent i = getIntent();	
		 
		txtName = (TextView) findViewById(R.id.inputName);
        txtTime = (TextView) findViewById(R.id.inputTime);
        txtCalories = (TextView) findViewById(R.id.inputCalories);
       // txtScore = (TextView) findViewById(R.id.inputScore);
        txtingredients = (TextView) findViewById(R.id.inputIngredients);
        txtInstructions = (TextView) findViewById(R.id.inputInstructions);
        btnAddFavorite = (Button) findViewById(R.id.addFavorites);
        db = new DatabaseHandler(this);
        session = new SessionManagement(this);
        // getting product id (id) from intent
        id = i.getStringExtra(TAG_ID);
        String view = i.getStringExtra("view");
        if(view.equalsIgnoreCase("true")){
        	btnAddFavorite.setClickable(false);
        	btnAddFavorite.setEnabled(false);
        }
         // Getting complete product details in background thread
         AsyncTask<String, String, String> r = new GetRecipe().execute();
         
     // Create button
          
          
          btnAddFavorite.setOnClickListener(new View.OnClickListener() {
   			
   			@Override
   			public void onClick(View v) {
   				HashMap<String,String> user;
				userloggedin = db.isUserLoggedIn(getApplicationContext());
				
				if(userloggedin){
            	//	db.addRecipes(TAG_RNAME, TAG_RTIME, TAG_RCALORIES, TAG_RINSTRUCTIONS);
					Intent intent = new Intent(DisplayRecipe.this, AddFavoriteRecipe.class);
	   		    	intent.putExtra(TAG_ID, id);
	   		    	// starting new activity and expecting some response back
	   	            startActivityForResult(intent, 100);
            	}else{
            		Dialog d = session.checkLogin();
        			d.show();
            	}
   				
   			
   			}
   		});
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_recipe, menu);
		return true;
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
	
	
	

/**
 * Background Async Task to Get complete product details
 * */
class GetRecipe extends AsyncTask<String, String, String> {
	
	

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       
        pDialog = new ProgressDialog(DisplayRecipe.this);
        pDialog.setMessage("Loading recipe details. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    
     
    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
    	super.onPostExecute(file_url);
    	runOnUiThread(new Runnable() {
    		
    		@Override
    		public void run() {
    			if(recipesM!=null){
    				// display recipe data in EditText
    				
					try {
						String n;
						n = recipesM.getString(TAG_NAME);
						txtName.setText("NAME			:" + n);
	                    String t= recipesM.getString(TAG_TIME);
	                    txtTime.setText("COOKING TIME	:" + t);
	                    String c=recipesM.getString(TAG_CALORIES);
	                    txtCalories.setText("TOTAL CALORIES   :"+c);
	                    //txtScore.setText("TOTAL SCORE   :"+recipes.getString(TAG_SCORE));
	                    String in=recipesM.getString(TAG_INGREDIENTS);
	                    txtingredients.setText("INGREDIENTS   :"+ in);
	                    String i=recipesM.getString(TAG_INSTRUCTIONS);
	                    txtInstructions.setText("INSTRUCTIONS   :"+ i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   
    			}
    			
    		}
    	});
    	
    	pDialog.dismiss();
    	
    } 
    
    

	
	protected String doInBackground(String... args) {
				int success;
				
				try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id2", id));
                System.out.println("id number************************"+ id);                   
               
                
             /* getting recipe details by making HTTP request
                 Note that recipe details url will use GET request */
                JSONObject json = jParser.makeHttpRequest(url_display_recipe, "GET", params);
                
             // check your log for json response
                Log.d("Single Recipe Details", json.toString());
                
             // json success tag
                
					success = json.getInt(TAG_SUCCESS);
					
                   
                    if (success == 1) {
                      	JSONArray recipeObj = json.getJSONArray(TAG_RECIPES); // JSON Array

                        // get first product object from JSON Array
                        JSONObject recipes = recipeObj.getJSONObject(0);
                        recipesM = recipes;
                                    
                        
                    }	
					
					
				} catch (JSONException e) {
				         	e.printStackTrace();
				}catch (Exception e){
					       e.printStackTrace();
				}
               
           		
		return null;
	 }
  }    
  



}
             











	
	


	