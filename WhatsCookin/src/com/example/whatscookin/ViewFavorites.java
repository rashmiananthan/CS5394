package com.example.whatscookin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.whatscookin.DisplayResults.LoadResults;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewFavorites extends ListActivity {
	
	private static String url_get_favorites = "http://whatscookinadmin.dukealums.com/get_favorites.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_RECIPES = "recipes";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
    private static final String TAG_TIME = "time";
    private static final String TAG_SCORE = "score";
    private static final String TAG_CALORIES = "calories";
	 // Progress Dialog
    private ProgressDialog pDialog;

	TextView txtName;
	 DatabaseHandler db;
	 boolean userloggedin;
	 SessionManagement session;
	private String uemail;
	 
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_favorites);
		// This is needed only if we use SQLite. But keeping it for now
		db = new DatabaseHandler(this);
		session = new SessionManagement(this);
		userloggedin = db.isUserLoggedIn(getApplicationContext());
		uemail = db.getUserDetails().get("email");
		
		 new LoadResults().execute();
		
		if(userloggedin){
			
			
			ListView l = getListView();
			
			l.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					 String id_a = ((TextView) view.findViewById(R.id.id)).getText()
		                        .toString();
					// Starting new intent
		                Intent in = new Intent(getApplicationContext(),
		                        DisplayRecipe.class);
		                // sending id to next activity
		                in.putExtra(TAG_ID, id_a);
		                in.putExtra("view", "true");
		                // starting new activity and expecting some response back
		                startActivityForResult(in, 100);
					
				}
				
			});
			
			//setupActionBar();			
			
    	}else{
    		Dialog d = session.checkLogin();
			d.show();
    	}
		
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
		getMenuInflater().inflate(R.menu.view_favorites, menu);
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
	 
	
	// Response from DisplayRecipe Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user .....
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
	
	  /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadResults extends AsyncTask<String, String, String> {
 
    	ArrayList<HashMap<String, String>> recipesList = new ArrayList<HashMap<String,String>>();
    	JSONArray recipes = null;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewFavorites.this);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            //pDialog.show();
            
            }

		@Override
		protected String doInBackground(String... arg0) {
			
			JSONParser jParser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uemail", uemail));
            JSONObject json = jParser.makeHttpRequest(url_get_favorites, "GET", params);
            
            Log.d("Favorite Recipes: ", json.toString());
            
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    recipes = json.getJSONArray(TAG_RECIPES);
 
                    // looping through All Products
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject c = recipes.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String time = c.getString(TAG_TIME);
                        String calories = c.getString(TAG_CALORIES);
                        
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_CALORIES, calories);
                        map.put(TAG_TIME, time);
                        
                        // adding HashList to ArrayList
                        recipesList.add(map);
                    }
                } else {
                    // no recipes found

                    // Display text to show there are no results
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            
            
            
			return null;
		}
		
		
		 /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            ViewFavorites.this, recipesList,
                            R.layout.result_item, new String[] { TAG_ID,
                                    TAG_NAME, TAG_CALORIES, TAG_TIME},
                            new int[] { R.id.id, R.id.name, R.id.calories, R.id.time });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

		
        }
    }
   

        
        
}
