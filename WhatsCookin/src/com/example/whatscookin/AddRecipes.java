package com.example.whatscookin;

import android.annotation.TargetApi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import java.util.List;
import java.util.ArrayList;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



import android.os.AsyncTask;
import android.util.Log;





public class AddRecipes extends Activity {
	
	// Progress Dialog
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    
    EditText inputName;
    EditText inputIngredients;
    EditText inputTime;
    EditText inputCalories;
    EditText inputInstructions;
    
 // url to create new product
    private static String url_create_product = "http://whatscookinadmin.dukealums.com/create_product.php";
    
 // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = "SubmitActivity";

public int flag = 0;

	String[] vegetable ={"Egg Plant","Spinach"," Okra","Cabbage","Potato","Peas","Beans","Flower"}; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_recipes);
		// Show the Up button in the action bar.
	   //Creating the instance of ArrayAdapter containing list of language names  
        ArrayAdapter<String> adapter = new ArrayAdapter<String>  
         (this,android.R.layout.select_dialog_item,vegetable);  
     //Getting the instance of AutoCompleteTextView  
        AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputIngredients);  
        actv.setThreshold(1);//will start working from first character  
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView  
        actv.setTextColor(Color.RED); 
		setupActionBar();
		
		// Edit Text
        inputName = (EditText) findViewById(R.id.inputName);
        inputIngredients = (EditText) findViewById(R.id.inputIngredients);
        inputTime = (EditText) findViewById(R.id.inputTime);
        inputCalories = (EditText) findViewById(R.id.inputCalories);
        inputInstructions = (EditText) findViewById(R.id.inputInstructions);
 
        // Create button
        Button btnNewRecipe = (Button) findViewById(R.id.btnNewRecipe);
 
        // button click event
        btnNewRecipe.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // creating new product in background thread
              new CreateNewRecipe().execute();
          
            	
            	}
        });

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
		getMenuInflater().inflate(R.menu.add_recipes, menu);
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
	
	
	
	
	
	
	/**
     * Background Async Task to Create new product
     * */
    class CreateNewRecipe extends AsyncTask<String, String, String> {
    	
    	
       
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddRecipes.this);
            pDialog.setMessage("Inserting Recipe ..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	
        	String name = inputName.getText().toString();
            String ingredients = inputIngredients.getText().toString();
            String time = inputTime.getText().toString();
            String calories = inputCalories.getText().toString();
            String instructions = inputInstructions.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            
        	 params.add(new BasicNameValuePair("name", name));
             params.add(new BasicNameValuePair("ingredients", ingredients));
             params.add(new BasicNameValuePair("time", time));
             params.add(new BasicNameValuePair("calories", calories));
             params.add(new BasicNameValuePair("instructions", instructions));
            
             Log.d(TAG, "before sending input to localhost");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,"POST", params);
 
            // check log cat from response
            Log.d(TAG, json.toString());
 
            // check for success tag
            try {
                //int s*uccess = json.getInt(TAG_SUCCESS);
 
                if ((json.getInt(TAG_SUCCESS)) == 1) {
                    // successfully created product
                 	Log.d(TAG, "inserting successful");
                			flag = 1;
                } else {
                    // failed to create product
                	Log.d(TAG, "inserting not successful");
                		flag = 0;
                		
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
            // dismiss the dialog once done
           pDialog.dismiss();
           AlertDialog alertDialog = new AlertDialog.Builder(AddRecipes.this).create();
           if(flag == 1){
           		flag = 0;
           		Log.d(TAG, "inside flag == 1");
           	// Setting Dialog Title
                alertDialog.setTitle("Submission successful");
             // Setting Dialog Message
                alertDialog.setMessage("Your recipe has been successfully submitted. You will be notified once the admin approves it.");
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
         
           }else{
           		Log.d(TAG, "inside flag == 0");
           	// Setting Dialog Title
                alertDialog.setTitle("Error Occurred");
             // Setting Dialog Message
                alertDialog.setMessage("Enter the details again. Sorry for the inconvenience.");
             // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                       	 Intent dashboard = new Intent(getApplicationContext(), AddRecipes.class);
                       	 startActivity(dashboard); 
                        }
                });

                // Showing Alert Message
                alertDialog.show();
            	
           	}
        }
 
    }
}

	
	
	
	
	
	
	
	  

