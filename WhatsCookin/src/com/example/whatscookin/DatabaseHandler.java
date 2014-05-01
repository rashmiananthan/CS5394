package com.example.whatscookin;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;
 
    // Database Name
    private static final String DATABASE_NAME = "whatscookin";
 
    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_RECIPES = "recipes"; 
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
   
    private static final String KEY_RNAME = "recipe_name";
    private static final String KEY_RTIME = "recipe_time";
    private static final String KEY_RCALORIES = "recipe_calories";
    private static final String KEY_RINSTRUCTIONS = "recipe_instructions";
    
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE" + ")";
    
    private static final String CREATE_LOGIN_RECIPES = "CREATE TABLE " + TABLE_RECIPES + "("
            + KEY_RNAME + " TEXT,"  
    		+ KEY_RTIME + " INTEGER,"
    		+ KEY_RCALORIES + " INTEGER,"
    		+ KEY_RINSTRUCTIONS + " TEXT" +
    ")";
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
 // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d("DB", "Creating database "+db);
        db.execSQL(CREATE_LOGIN_RECIPES);
        Log.d("DB", "Creating database "+db);
    }
 
   
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
      
        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    
    public void addRecipes(String name, int time, int calories, String instructions) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_RNAME, name); // Name
        values.put(KEY_RTIME, time); // Name
        values.put(KEY_RCALORIES, calories); // Name
        values.put(KEY_RINSTRUCTIONS, instructions); // Name
          Log.d("DB", "Adding recipe name"+name+" time "+time+" calories "+ calories+ " insttructions"+instructions);  
        // Inserting Row
        db.insert(TABLE_RECIPES, null, values);
        db.close(); // Closing database connection
    }
     
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        int count = getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
    
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        resetTables();
        return true;
    }
    
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
          
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
    
    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
         
        // return row count
        return rowCount;
    }
     
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.delete(TABLE_RECIPES, null, null);
        db.close();
    }
    
}
