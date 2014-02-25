package com.example.whatscookin;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.content.Intent;


public class MainActivity extends Activity {

	SessionManagement session;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		session = new SessionManagement(this);
		db = new DatabaseHandler(this);
		
		boolean notNowEnabled = session.isNotNow();
		boolean isuserloggedin = db.isUserLoggedIn(this);
		
		if((notNowEnabled)||(isuserloggedin)){
			Intent i = new Intent(this, HomePage.class);
			this.startActivity(i);
		}else
		{
			Dialog d = session.checkLogin();
			d.show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
