package com.feutech.whatthehack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlacesActivity extends Activity {

	ListView placesListView;
	ArrayList<Place> places;
	
	private static final long delay = 2000L;
    private boolean mRecentlyBackPressed = false;
    private Handler mExitHandler = new Handler();
    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mRecentlyBackPressed=false;   
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String username = sharedPreferences.getString("_username", "");
		String password = sharedPreferences.getString("_password", "");
		boolean hasLogged = sharedPreferences.getBoolean("_hasLogged", false);
		Toast.makeText(getApplicationContext(), username + " " + password + " " + hasLogged, Toast.LENGTH_SHORT).show();
		
		places = new ArrayList<Place>();

		// START for testing only
		places.add(new Place("New Me"));
		places.add(new Place("Manila"));
		places.add(new Place("Makati"));
		places.add(new Place("Quezon City"));
		// END for testing only

		PlacesAdapter placesAdapter = new PlacesAdapter(this, places);
		placesListView = (ListView) findViewById(R.id.places_listView);
		placesListView.setAdapter(placesAdapter);
		placesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(PlacesActivity.this.getApplicationContext(),
						PlacesActivity.this.places.get(position).name,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.places, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			Intent intent = new Intent(PlacesActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void postBtn(View v) {
		dispatchTakePictureIntent();
		Log.d("PlacesActivity", "Inside postBtn");
	}
	
	static final int REQUEST_TAKE_PHOTO = 1;
	private File photoFile;

	private void dispatchTakePictureIntent() {
		Log.d("PlacesActivity", "dispatching take picture intent");
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            Log.d("PlacesActivity", ex.getMessage());
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        } else {
	        	Log.d("PlacesActivity", "image file is null");
	        }
	    } else {
	    	Toast.makeText(getApplicationContext(), "No camera app installed", Toast.LENGTH_SHORT).show();
	    }
	}
	
	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
		Log.d("PlacesActivity", "inside createImageFile()");
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = this.getCacheDir();
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
	    	/**
	        Bundle extras = data.getExtras();
	        
	        //optional na lang siguro ito ano?
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        
	        */
	        
	        
	        String filePhotoPath = photoFile.getAbsolutePath();
	        Intent i = new Intent(PlacesActivity.this, PostStatusActivity.class);
	        i.putExtra(PostStatusActivity.PostStatusActivity_PhotoPath, filePhotoPath);
	        startActivity(i);
	        //mImageView.setImageBitmap(imageBitmap);
	    }
	}
	@Override
    public void onBackPressed() {
        if (mRecentlyBackPressed) {
            mExitHandler.removeCallbacks(mExitRunnable);
            mExitHandler = null;
            super.onBackPressed();
            finish();
        } else {
            mRecentlyBackPressed = true;
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
            mExitHandler.postDelayed(mExitRunnable, delay);
        }
    }
}

class Place {
	String name;

	public Place(String ucName) {
		this.name = ucName;
	}
}

class PlacesAdapter extends ArrayAdapter<Place> {
	Activity context;
	List<Place> places = new ArrayList<Place>();

	public PlacesAdapter(Activity context, List<Place> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
		this.places = objects;
		this.context = context;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.list_item_places, null);

		// This is once we get the background from the web
		// convertView.setBackground(background);

		TextView placeName = (TextView) convertView
				.findViewById(R.id.places_name_tv);
		placeName.setText(places.get(position).name);

		return convertView;
	}
}
