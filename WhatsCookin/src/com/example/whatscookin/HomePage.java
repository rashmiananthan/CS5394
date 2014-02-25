package com.example.whatscookin;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.content.*;

import java.util.*;
import android.app.*;



public class HomePage extends Activity {

	Button searchRecipes;
	Button addRecipes;
	Button viewFavorites;
	Button logout;
	TextView username;
	TextView lutext;
	final Context context = this;
	boolean userloggedin;
	DatabaseHandler db;
	SessionManagement session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		
		searchRecipes = (Button)findViewById(R.id.button1);
		addRecipes = (Button)findViewById(R.id.button2);
		viewFavorites = (Button)findViewById(R.id.button3);
		logout = (Button)findViewById(R.id.button4);
		username = (TextView)findViewById(R.id.username);
		lutext = (TextView)findViewById(R.id.username);
		
		db = new DatabaseHandler(this);
		session = new SessionManagement(this);
		HashMap<String,String> user;
		userloggedin = db.isUserLoggedIn(getApplicationContext());
		
		if(userloggedin){
			user = db.getUserDetails();
			username.setText("Welcome "+user.get("name"));
		}else{
			username.setText("Welcome Guest");
			lutext.setText("You will enjoy the privileges of contributing your recipes, saving your favorites and viewing them if you are signed in/logged in");
			logout.setVisibility(View.INVISIBLE);
		}
		searchRecipes.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
            		Intent i = new Intent(getApplicationContext(), SearchRecipes.class);
            		startActivity(i);
            }
          });
		addRecipes.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
            	if(userloggedin){
            		Intent i = new Intent(getApplicationContext(), AddRecipes.class);
            		startActivity(i);
            	}else{
            		Dialog d = session.checkLogin();
        			d.show();
            	}
            }
          });
		viewFavorites.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
            	if(userloggedin){
            		Intent i = new Intent(getApplicationContext(), ViewFavorites.class);
            		startActivity(i);
            	}else{
            		Dialog d = session.checkLogin();
        			d.show();
            	}
            }
          });
		logout.setOnClickListener(new View.OnClickListener() {         
            @SuppressWarnings("deprecation")
			public void onClick(View view) {
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            	// Setting Dialog Title
                alertDialog.setTitle("Logout");
             // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to logout ?");
             // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                       	  db.logoutUser(HomePage.this);
                       	  Dialog d = session.afterLogout();
                       	  d.show();
                       	  
                   	  }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    	dialog.cancel();
                   	  
               	  }
            });
             // Showing Alert Message
                alertDialog.show();
            }
          });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}

}
