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
	public final static String PLACE_NAME = "place_name";
	public final static String LATITUDE = "lat";
	public final static String LONGITUDE = "long";
	public final static String STATUS_MESSAGE = "statusMessage";
	public final static String PHOTO = "photo";
	public final static String CATEGORY = "category";
	public final static String DATE_POSTED = "datePoster";
	
	public final static String UPVOTE = "upVote";
	public final static String DOWNVOTE = "downVote";
	public final static String TOTAL_UPVOTE = "totalUpvote";
	public final static String TOTAL_DOWNVOTE = "totalDownvote";
	
	protected final static String DROP_TABLE = "DROP TABLE IF EXISTS " + T_NAME;
	protected final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ T_NAME + " (" +
												POST_ID + " INTEGER PRIMARY KEY, " +
												USERNAME + " TEXT, " + 
												PLACE_NAME + " TEXT, " + 
												LATITUDE + " NUMBER, " + 
												LONGITUDE + " NUMBER, " + 
												STATUS_MESSAGE + " TEXT, " + 
												PHOTO + " TEXT, " + 
												CATEGORY + " TEXT, " + 
												DATE_POSTED + " TEXT, " + 
												UPVOTE + " INTEGER, " + 
												DOWNVOTE + " INTEGER, " + 
												TOTAL_UPVOTE + " INTEGER, " + 
												TOTAL_DOWNVOTE + " INTEGER)";
	
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
				String place_name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
				double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
				String statusMessage = cursor.getString(cursor.getColumnIndex(STATUS_MESSAGE));
				String photo = cursor.getString(cursor.getColumnIndex(PHOTO));
				String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
				String datePosted = cursor.getString(cursor.getColumnIndex(DATE_POSTED));
				int upvote = cursor.getInt(cursor.getColumnIndex(UPVOTE));
				int downvote = cursor.getInt(cursor.getColumnIndex(DOWNVOTE));
				int totalUpvote = cursor.getInt(cursor.getColumnIndex(TOTAL_UPVOTE));
				int totalDownvote = cursor.getInt(cursor.getColumnIndex(TOTAL_DOWNVOTE));
				
				listOfPosts.add(new Post(post_id, username, place_name,
						latitude, longitude, statusMessage, photo, category,
						datePosted, upvote, downvote, totalUpvote,
						totalDownvote));
				
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
			String place_name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
			double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
			double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
			String statusMessage = cursor.getString(cursor.getColumnIndex(STATUS_MESSAGE));
			String photo = cursor.getString(cursor.getColumnIndex(PHOTO));
			String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
			String datePosted = cursor.getString(cursor.getColumnIndex(DATE_POSTED));
			int upvote = cursor.getInt(cursor.getColumnIndex(UPVOTE));
			int downvote = cursor.getInt(cursor.getColumnIndex(DOWNVOTE));
			int totalUpvote = cursor.getInt(cursor.getColumnIndex(TOTAL_UPVOTE));
			int totalDownvote = cursor.getInt(cursor.getColumnIndex(TOTAL_DOWNVOTE));
			
			return new Post(post_id, username, place_name, latitude, longitude,
					statusMessage, photo, category, datePosted, upvote,
					downvote, totalUpvote, totalDownvote);
		}
		
		return null;
	}
	
	public boolean insert(Post post){
		ContentValues values = new ContentValues();
		
		values.put(POST_ID, post.getPost_id());
		values.put(USERNAME, post.getUsername());
		values.put(PLACE_NAME, post.getPlacename());
		values.put(LATITUDE, post.getLatitude());
		values.put(LONGITUDE, post.getLongitude());
		values.put(STATUS_MESSAGE, post.getStatusMessage());
		values.put(PHOTO, post.getPhoto());
		values.put(CATEGORY, post.getCategory());
		values.put(DATE_POSTED, post.getDatePosted());
		values.put(UPVOTE, post.getUpvote());
		values.put(DOWNVOTE, post.getDownvote());
		values.put(TOTAL_UPVOTE, post.getTotalUpvote());
		values.put(TOTAL_DOWNVOTE, post.getTotaldownVote());
		
		return this.database.insert(T_NAME, null, values)>0;
	}

	public boolean updateVotes(int post_id, String vote) {
		
		Cursor cursor = this.database.rawQuery("SELECT * FROM " + T_NAME + " WHERE " + POST_ID + "='" + post_id + "'", null);
		
		if (cursor.getCount() == 1) {
			ContentValues values = new ContentValues();
			
			Post post = getPost(post_id);

			Log.i("TAG", vote);
			if (vote.equals("upvote")) {
				values.put(UPVOTE, post.getUpvote() == 0 ? 1 : 0);
				values.put(TOTAL_UPVOTE, post.getUpvote() == 0 ? post.getTotalUpvote() + 1 : post.getTotalUpvote() - 1);
			}
			
			else {
				values.put(DOWNVOTE, post.getDownvote() == 0 ? 1 : 0);
				values.put(TOTAL_DOWNVOTE, post.getDownvote() == 0 ? post.getTotaldownVote() + 1 : post.getTotaldownVote() - 1);
			}
			
			return this.database.update(T_NAME, values, POST_ID + "='" + post_id + "'", null) > 0;
		}
		
		return false;
	}
	
	public boolean deleteAll(){
		return this.database.delete(T_NAME, null, null) > 0;
	}
}