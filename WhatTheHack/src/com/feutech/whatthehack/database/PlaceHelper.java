package com.feutech.whatthehack.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feutech.whatthehack.model.Place;

public class PlaceHelper {

	public final static String T_NAME = "places";
	public final static String PLACE_NAME = "place_name";
	public final static String PLACE_PICTURE = "place_picture";
	
	protected final static String DROP_TABLE = "DROP TABLE IF EXISTS " + T_NAME;
	protected final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ T_NAME + " (" +
												PLACE_NAME + " TEXT " + 
												PLACE_PICTURE + " TEXT )";
	
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper helper;
	
	public PlaceHelper(Context context){
		super();
		this.context = context;
	}
	
	public void create(){
		helper.onCreate(database);
		Log.i("Places_HELPER", "Table " + T_NAME + " created");
	}

	public void drop(){
		helper.onUpgrade(database, 0, 0);
	}
	
	public void open(){
		this.helper = new DatabaseHelper(context, CREATE_TABLE, DROP_TABLE);
		this.database = this.helper.getWritableDatabase();
		Log.i("Places_HELPER", "Database opened");
	}
	
	public void close(){
		this.helper.close();
		this.database.close();
		Log.i("Places_HELPER", "Database closed");
	}
	
	public Boolean isOpen(){
		return this.database.isOpen();
	}
	
	public ArrayList<Place> getAllPlaces(){
		ArrayList<Place> listOfPlaces= new ArrayList<Place>();
		
		Cursor cursor = this.database.query(true, T_NAME, null, null, null, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()){
			do {
				String place_name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
				String place_picture = cursor.getString(cursor.getColumnIndex(PLACE_PICTURE));
				
				listOfPlaces.add(new Place(place_name, place_picture));
			} while (cursor.moveToNext());
			
		}
		if(cursor!=null && !cursor.isClosed()){
			cursor.close();
		}
		return listOfPlaces;
	}
	
	public boolean insert(Place place){
		ContentValues values = new ContentValues();
		
		values.put(PLACE_NAME, place.getPlace_name());
		values.put(PLACE_PICTURE, place.getPlace_picture());
		
		return this.database.insert(T_NAME, null, values)>0;
	}
	
	public boolean deleteAll(){
		return this.database.delete(T_NAME, null, null) > 0;
	}
}