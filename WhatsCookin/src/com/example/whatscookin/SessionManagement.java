package com.example.whatscookin;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {

	SharedPreferences pref;
	Editor editor;
	Context context;
	
	int PRIVATE_MODE = 0, flag = 0;
	private static final String PREF_NAME = "Whatscookin";
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String IS_NOTNOW = "NotNowEnabled"; 
	
	public SessionManagement(Context con){
		this.context = con;
		pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	public Dialog checkLogin(){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Would you like to sign in / log in ?");
			builder.setItems(new CharSequence[]{"Sign Up!", "Log In", "Not now Thanks!"}, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							switch(which){
								case 0:{
									launchSignUp();
									break;
								}
								case 1:{
									launchLogIn();
									break;
								}
								case 2:{
									flag = 2;
									editor.putBoolean(IS_NOTNOW, true);
									launchHomePage();
									break;
								}
							}
						}
					});
			return builder.create();
	}
	
	public Dialog afterLogout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Would you like to log in back?");
		builder.setItems(new CharSequence[]{"Log In", "Not now Thanks!"}, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch(which){
							case 0:{
								launchLogIn();
								break;
							}
							case 1:{
								flag = 2;
								editor.putBoolean(IS_NOTNOW, true);
								launchHomePage();
								break;
							}
						}
					}
				});
		return builder.create();
}

	
	public void launchSignUp(){
		Intent i = new Intent(context, SignUp.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	public void launchLogIn(){
		Intent i = new Intent(context, LogIn.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	public void launchHomePage(){
		Intent i = new Intent(context, HomePage.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
	
	public boolean isNotNow(){
		return pref.getBoolean(IS_NOTNOW, false);
	}
}
