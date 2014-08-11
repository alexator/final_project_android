package com.geminnu.hexagon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	//Database information
	private static final String DATABASE_NAME = "medicalRecord";
	private static final int DATABASE_VERSION = 1;
	
	//Tables
	public static final String TABLE_READINGS = "readings";
	public static final String TABLE_PROFILES = "profiles";
	
	//Common columns in both tables
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USER_ID = "user_id";
	
	//Readings' Table
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_TIME = "created_at";
	public static final String COLUMN_STATUS = "status";
	
	//Profiles' Table
	public static final String COLUMN_FIRSTNAME = "first_name";
	public static final String COLUMN_LASTNAME = "last_name";
	public static final String COLUMN_AGE = "age";
	public static final String COLUMN_SEX = "sex";
	  

	// Table Readings creation SQL statement
	private static final String CREATE_TABLE_READINGS = "create table "
			+ TABLE_READINGS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_STATUS 
			+ " text not null, " + COLUMN_VALUE
			+ " real not null, " + COLUMN_TYPE + " integer not null, " 
			+ COLUMN_USER_ID + " integer not null, " + COLUMN_TIME 
			+ " datetime default current_timestamp);";
	
	// Table Profiles creation SQL statement
	private static final String CREATE_TABLE_PROFILES = "create table "
			+ TABLE_PROFILES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_FIRSTNAME 
			+ " text not null, " + COLUMN_LASTNAME
			+ " text not null, " + COLUMN_AGE + " integer not null, " 
			+ COLUMN_USER_ID + " integer not null, " + COLUMN_SEX 
			+ " text not null);";
	  
	
	public MySQLiteHelper(Context context) {
		//init
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		//Create Readings
		database.execSQL(CREATE_TABLE_READINGS);
		
		//Create Profiles
		database.execSQL(CREATE_TABLE_PROFILES);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
		onCreate(db);
	}
}
