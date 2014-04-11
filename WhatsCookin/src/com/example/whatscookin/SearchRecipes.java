package com.example.whatscookin;


import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SearchRecipes extends ListActivity implements OnItemSelectedListener {

	private Spinner calories_spinner;
	private Spinner cook_time_spinner;
	//public final static String EXTRA_MESSAGE = "sending ingredients";
	//ArrayList<HashMap<String, String>> ingredientList;	
	ArrayList<String> ingredientList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ingredientList = new ArrayList<String>();
		
		setContentView(R.layout.activity_search_recipes);
		// Show the Up button in the action bar.
		setupActionBar();
		
		calories_spinner = (Spinner) findViewById(R.id.calories_spinner);
		calories_spinner.setOnItemSelectedListener(this);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.calories_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		calories_spinner.setAdapter(adapter);	
		
		cook_time_spinner = (Spinner) findViewById(R.id.cook_time_spinner);
		cook_time_spinner.setOnItemSelectedListener(this);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.cook_time_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		cook_time_spinner.setAdapter(adapter2);	
		
	}

    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	if(parent == calories_spinner)
    	{
    		String calories_selection = (String)calories_spinner.getItemAtPosition(pos);
    	}
    	else if(parent == cook_time_spinner)
    	{
    		String calories_selection = (String)cook_time_spinner.getItemAtPosition(pos);
    	}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void onSearchClicked(View view)
	{
		if(ingredientList.isEmpty())
		{	//If the user hasn't entered any ingredients, prompt them to add ingredient.
		    DialogFragment newFragment = new ErrorMessage();
		    newFragment.show(getFragmentManager(), "missing_ingredient");
		}
		else
		{
			//If the user has entered ingredients, Display results activity
	    	Intent intent = new Intent(this, DisplayResults.class);
	    	intent.putStringArrayListExtra("ingredient list", ingredientList);
	    	//intent.putExtra(EXTRA_MESSAGE, ingredientList);
	    	startActivity(intent);				
		}
		
	}
	
	public void addIngredient(View view)
	{
    	EditText editText = (EditText) findViewById(R.id.new_ingredient);
    	String newIngredient = editText.getText().toString();
    	/*
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("ingredient", newIngredient);
    	*/
    	ingredientList.add(newIngredient);
    	
    	/*
    	 ListAdapter adapter = new SimpleAdapter(
                 SearchRecipes.this, ingredientList,
                 R.layout.ingredient_item, new String[] { "ingredient"},
                 new int[] { R.id.ingredient });
         // updating listview
         setListAdapter(adapter);*/
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchRecipes.this,R.layout.ingredient_item, R.id.ingredient, ingredientList);
    	setListAdapter(adapter);
    	
        //ListAdapter adapter = new SimpleAdapter(SearchRecipes.this, ingredientList, R.layout.ingredient_item, R.id.ingredient );
        // updating listview
        //setListAdapter(adapter);    	
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
		getMenuInflater().inflate(R.menu.search, menu);
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

}
