package com.example.whatscookin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 





import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 





import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class DisplayResults extends ListActivity implements OnItemSelectedListener {

    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<String> ingredients;
    ArrayList<HashMap<String, String>> recipesList;
 
    // url to get all products list
    private static String url_get_results = "http://whatscookinadmin.dukealums.com/search/get_results.php";
 
    private Spinner sort_by_spinner;
    
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECIPES = "recipes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TIME = "time";
    private static final String TAG_SCORE = "score";
    private static final String TAG_CALORIES = "calories";
   // private static final String QUERY = "onion";
    // products JSONArray
    JSONArray recipes = null;
    private String calories;
    private String time;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	        setContentView(R.layout.activity_display_results);
	 
	        // Hashmap for ListView
	        Intent intent = getIntent();
	        ingredients = new ArrayList<String>();
	        ingredients = intent.getStringArrayListExtra("ingredient list");
	        recipesList = new ArrayList<HashMap<String, String>>();
	        calories = intent.getStringExtra("calories");
	        time = intent.getStringExtra("time");	 
	        // Loading recipes in Background Thread
	        new LoadResults().execute();
	 
	        sort_by_spinner = (Spinner) findViewById(R.id.sort_by_spinner);
	        sort_by_spinner.setOnItemSelectedListener(this);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.sort_by_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			sort_by_spinner.setAdapter(adapter);
			
	        ListView l = getListView();
	 
	        l.setOnItemClickListener(new OnItemClickListener() {
	 
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                // getting values from selected ListItem
	                String id2 = ((TextView) view.findViewById(R.id.id)).getText()
	                        .toString();
	 
	                // Starting new intent
	                Intent in = new Intent(getApplicationContext(),
	                        DisplayRecipe.class);
	                // sending id to next activity
	                in.putExtra(TAG_ID, id2);
	                in.putExtra("view", "false");
	                // starting new activity and expecting some response back
	                startActivityForResult(in, 100);
	            }
	        });
	 
	    
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.display_results, menu);
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
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayResults.this);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
        	JSONArray  ingred = new JSONArray();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for(String s : ingredients)
            	ingred.put(s);
            	//System.out.println("Ingredient:"+s);
            	String inlist = ingred.toString();
            	params.add(new BasicNameValuePair("query", inlist));
            	params.add(new BasicNameValuePair("calories", calories));
            	params.add(new BasicNameValuePair("time", time));
            	Log.d("Ingredient List: ", inlist);
            	Log.d("Calories: ", calories);
            	Log.d("Time: ", time);
            JSONObject json = jParser.makeHttpRequest(url_get_results, "GET", params);
 
            Log.d("Recipes: ", json.toString());
 
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
                        String score = c.getString(TAG_SCORE);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_CALORIES, calories);
                        map.put(TAG_TIME, time);
                        map.put(TAG_SCORE, score);
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
                            DisplayResults.this, recipesList,
                            R.layout.result_item, new String[] { TAG_ID,
                                    TAG_NAME, TAG_CALORIES, TAG_TIME},
                            new int[] { R.id.id, R.id.name, R.id.calories, R.id.time });
                    // updating listview
                    setListAdapter(adapter);
                	TextView numResults = (TextView) findViewById(R.id.num_results);
                	numResults.setText("Results: " + recipesList.size());
                }
            });
 
        }
 
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
	{
		String sort = "";
		if(pos == 0)		sort = "score";
		else if(pos == 1)	sort = "calories";
		else if(pos == 2)	sort = "time";
		else if(pos == 3)	sort = "name";

		Log.d("sort by: ", sort);
		randomizedQuickSort(recipesList, 0, recipesList.size()-1, sort);
        ListAdapter adapter = new SimpleAdapter(
                DisplayResults.this, recipesList,
                R.layout.result_item, new String[] { TAG_ID, TAG_NAME, TAG_CALORIES, TAG_TIME},
                new int[] { R.id.id, R.id.name, R.id.calories, R.id.time });
        // updating listview
        setListAdapter(adapter);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// interface callback
		
	}
	
	public static void randomizedQuickSort(ArrayList<HashMap<String, String>> list, int p, int r, String sort)
	{
		if (p < r)
		{
			int q = randomizedPartition(list, p, r, sort);
			randomizedQuickSort(list, p, q - 1, sort);
			randomizedQuickSort(list, q + 1, r, sort);
		}
	}

	public static int randomizedPartition(ArrayList<HashMap<String, String>> list, int p, int r, String sort)
	{
		int i = (int)(Math.random() * (r - p + 1)) + p;
		swap(list, r, i);
		return partition(list, p, r, sort);
	}
	
	public static int partition(ArrayList<HashMap<String, String>> list, int p, int r, String sort)	// O(n)
	{	String rValue = list.get(r).get(sort);	//int x = list[r];
		int i = p - 1;
		for(int j = p; j <= r - 1; j++)					// O(n)
		{	String jValue = list.get(j).get(sort);
			if(jValue.compareTo(rValue) <= 0)	//list[j] <= x)
			{	i++;
				swap(list, i, j);
			}
		}
		swap(list, i + 1, r);
		return i + 1;
	}
	
	public static void swap(ArrayList<HashMap<String, String>> list, int i, int j)
	{	
		HashMap<String,String> temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}	

}
