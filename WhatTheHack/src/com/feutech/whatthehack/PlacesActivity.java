package com.feutech.whatthehack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.feutech.whatthehack.adapters.ListViewPlacesAdapter;
import com.feutech.whatthehack.database.PlaceHelper;
import com.feutech.whatthehack.model.Place;

public class PlacesActivity extends Activity implements OnItemClickListener{

	private ListView placesListView;
	private ArrayList<Place> places;
	
	private ListViewPlacesAdapter adapter;
	
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
		
		places = new ArrayList<Place>();
		placesListView = (ListView) findViewById(R.id.places_listView);
		
		places = getPlacesFromDB();
		
		adapter = new ListViewPlacesAdapter(this, places);
		
		placesListView.setAdapter(adapter);
		placesListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
	}

	private ArrayList<Place> getPlacesFromDB() {
		
		PlaceHelper helper = new PlaceHelper(this);
		helper.open();
		helper.create();
		ArrayList<Place> places = helper.getAllPlaces();
		helper.close();
		
		return places;
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
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			
			editor.putString("_username", "");
			editor.putString("_password", "");
			editor.putBoolean("_hasLogged", false);
			editor.commit();
			
			startActivity(new Intent(PlacesActivity.this, LoginActivity.class));
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
