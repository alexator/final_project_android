package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
		onCreate(db);
	}
	
	// Adding new contact
    public void addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRSTNAME, profile.getFirstName()); // Contact Name
        values.put(COLUMN_LASTNAME, profile.getLastName()); // Contact Phone
        values.put(COLUMN_AGE, profile.getAge()); // Contact Phone
        values.put(COLUMN_USER_ID, profile.getUserId()); // Contact Phone
        values.put(COLUMN_SEX, profile.getSex()); // Contact Phone
 
        // Inserting Row
        db.insert(TABLE_PROFILES, null, values);
        db.close(); // Closing database connection
    }
    
    public void addReading(Reading reading) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, reading.getStatus()); // Contact Name
        values.put(COLUMN_VALUE, reading.getValue()); // Contact Phone
        values.put(COLUMN_TYPE, reading.getType()); // Contact Phone
        values.put(COLUMN_USER_ID, reading.getUserId()); // Contact Phone
 
        // Inserting Row
        db.insert(TABLE_READINGS, null, values);
        db.close(); // Closing database connection
    }
 
 
    // Getting single contact
   public Profile getProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_PROFILES, new String[] { COLUMN_ID, COLUMN_FIRSTNAME,
        		COLUMN_LASTNAME, COLUMN_AGE, COLUMN_USER_ID, COLUMN_SEX }, COLUMN_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Profile profile = new Profile(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(5), cursor.getInt(3), cursor.getInt(4));
        // return contact
        return profile;
    }
   // Getting single contact
   public Reading getUserLatestReading(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_READINGS, new String[] { COLUMN_ID, COLUMN_STATUS,
        		COLUMN_VALUE, COLUMN_TYPE, COLUMN_USER_ID, COLUMN_TIME }, COLUMN_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToLast();
 
        Reading reading = new Reading(cursor.getInt(0), cursor.getDouble(2),
                cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getString(1));
        // return contact
        return reading;
    }
   
// Getting single contact
   public List<Reading> getUserAllReading(int id) {
	   List<Reading> listReadings = new ArrayList<Reading>();
	   String selectQuery = "SELECT  * FROM " + TABLE_READINGS + " WHERE " + COLUMN_USER_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.moveToFirst()) {
        	do {
        		Reading reading = new Reading();
        		reading.setId(cursor.getInt(0));
        		reading.setValue(cursor.getDouble(2));
        		reading.setStatus(cursor.getString(1));
        		reading.setType(cursor.getInt(3));
        		reading.setUserId(cursor.getInt(4));
        		reading.setTime(cursor.getString(5));
        		listReadings.add(reading);
        	}	while(cursor.moveToNext());
        }
 
        return listReadings;
    }
   
   public List<Reading> getUserReadingBySensor(int userId, int sensorId) {
	   List<Reading> listReadings = new ArrayList<Reading>();
	   String selectQuery = "SELECT  * FROM " + TABLE_READINGS + " WHERE " + COLUMN_USER_ID + " = " + userId + " AND " + COLUMN_TYPE + " = " + sensorId ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.moveToFirst()) {
        	do {
        		Reading reading = new Reading();
        		reading.setId(cursor.getInt(0));
        		reading.setValue(cursor.getDouble(2));
        		reading.setStatus(cursor.getString(1));
        		reading.setType(cursor.getInt(3));
        		reading.setUserId(cursor.getInt(4));
        		reading.setTime(cursor.getString(5));
        		listReadings.add(reading);
        	}	while(cursor.moveToNext());
        }
 
        return listReadings;
    }

   public List<Reading> getUserBadReading(int id) {
	   List<Reading> listReadings = new ArrayList<Reading>();
	   String selectQuery = "SELECT  * FROM " + TABLE_READINGS + " WHERE " + COLUMN_USER_ID + " = " + id + " AND " + COLUMN_STATUS + " = \"BAD\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.moveToFirst()) {
        	do {
        		Reading reading = new Reading();
        		reading.setId(cursor.getInt(0));
        		reading.setValue(cursor.getDouble(2));
        		reading.setStatus(cursor.getString(1));
        		reading.setType(cursor.getInt(3));
        		reading.setUserId(cursor.getInt(4));
        		reading.setTime(cursor.getString(5));
        		listReadings.add(reading);
        	}	while(cursor.moveToNext());
        }
 
        return listReadings;
    }
}
