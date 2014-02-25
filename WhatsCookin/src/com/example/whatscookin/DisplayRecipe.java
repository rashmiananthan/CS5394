package com.example.whatscookin;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DisplayRecipe extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_recipe);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_recipe, menu);
		return true;
	}

}
