package com.feutech.whatthehack.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feutech.whatthehack.model.Post;


public class PostHelper {

	public final static String T_NAME = "post";
	public final static String POST_ID = "post_id";
	public final static String USERNAME = "username";
	public final static String LONGITUDE = "long";
	public final static String LATITUDE = "lat";
	public final static String REMARKS = "remarks";
	public final static String PHOTO = "photo";
	public final static String CATEGORY = "category";
	public final static String PLACE_NAME = "place_name";
	
	
	protected final static String DROP_TABLE = "DROP TABLE IF EXISTS " + T_NAME;
	protected final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ T_NAME + " (" +
												POST_ID + " INTEGER PRIMARY KEY " +
												USERNAME + " TEXT " + 
												LONGITUDE + " NUMBER " + 
												LATITUDE + " NUMBER " + 
												REMARKS + " TEXT " + 
												PHOTO + " TEXT " + 
												CATEGORY + " TEXT " + 
												PLACE_NAME + " TEXT )";
	
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper helper;
	
	public PostHelper(Context context){
		super();
		this.context = context;
	}
	
	public void create(){
		helper.onCreate(database);
		Log.i("Books_HELPER", "Table " + T_NAME + " created");
	}

	public void drop(){
		helper.onUpgrade(database, 0, 0);
	}
	
	public void open(){
		this.helper = new DatabaseHelper(context, CREATE_TABLE, DROP_TABLE);
		this.database = this.helper.getWritableDatabase();
		Log.i("Books_HELPER", "Database opened");
	}
	
	public void close(){
		this.helper.close();
		this.database.close();
		Log.i("Books_HELPER", "Database closed");
	}
	
	public Boolean isOpen(){
		return this.database.isOpen();
	}
	
	public ArrayList<Post> getAllPosts(){
		ArrayList<Post> listOfPosts= new ArrayList<Post>();
		
		Cursor cursor = this.database.query(true, T_NAME, null, null, null, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()){
			do {
				int post_id = cursor.getInt(cursor.getColumnIndex(POST_ID));
				String username = cursor.getString(cursor.getColumnIndex(USERNAME));
				double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
				double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
				String remarks = cursor.getString(cursor.getColumnIndex(REMARKS));
				String photo = cursor.getString(cursor.getColumnIndex(PHOTO));
				String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
				String place_name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
				
				listOfPosts.add(new Post(post_id, username, longitude,
						latitude, remarks, photo, category, place_name));
			} while (cursor.moveToNext());
			
		}
		if(cursor!=null && !cursor.isClosed()){
			cursor.close();
		}
		return listOfPosts;
	}
	
	public Post getPost(int postId) {
		Cursor cursor = this.database.rawQuery("SELECT * FROM " + T_NAME + " WHERE " + POST_ID + "='" + postId + "'", null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			int post_id = cursor.getInt(cursor.getColumnIndex(POST_ID));
			String username = cursor.getString(cursor.getColumnIndex(USERNAME));
			double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
			double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
			String remarks = cursor.getString(cursor.getColumnIndex(REMARKS));
			String photo = cursor.getString(cursor.getColumnIndex(PHOTO));
			String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
			String place_name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
			
			return new Post(post_id, username, longitude, latitude, remarks,
					photo, category, place_name);
		}
		
		return null;
	}
	
	public boolean insert(Post post){
		ContentValues values = new ContentValues();
		
		values.put(POST_ID, post.getPost_id());
		values.put(USERNAME, post.getUsername());
		values.put(LONGITUDE, post.getLongitude());
		values.put(LATITUDE, post.getLatitude());
		values.put(REMARKS, post.getRemarks());
		values.put(PHOTO, post.getPhoto());
		values.put(CATEGORY, post.getCategory());
		values.put(PLACE_NAME, post.getPlace_name());
		
		return this.database.insert(T_NAME, null, values)>0;
	}
	
	public boolean deleteAll(){
		return this.database.delete(T_NAME, null, null) > 0;
	}
}