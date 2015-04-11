package com.feutech.whatthehack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.listeners.GetAddressListener;
import com.feutech.whatthehack.listeners.PostListener;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.UserSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class PostStatusActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, GetAddressListener, View.OnClickListener, PostListener {

	// comment
	private ImageView photoIV;
	private TextView locationTV;
	private Button postBtn;
	private EditText postET;
	
	private GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	
	private Bitmap photo;
	
	private double longitude, latitude;

	public static final String TAG = "com.feutech.whatthehack.PostStatusActivity";
	public static final String PostStatusActivity_PhotoPath = "com.feutech.whatthehack.PostStatusActivity.PhotoPath";
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_status);

		photoIV = (ImageView) findViewById(R.id.PostActivity_imageIV);
		locationTV = (TextView) findViewById(R.id.PostStatus_location_TextView);
		postBtn = (Button) findViewById(R.id.PostStatus_post_Button);
		postET = (EditText) findViewById(R.id.PostStatus_post_EditText);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Posting...");
		progressDialog.setCancelable(false);
		
		String photoPath = getIntent().getStringExtra(PostStatusActivity_PhotoPath).trim();

		
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, o);
		
		photo = decodeFile(new File(photoPath));
		

		//set photo to screen
		if (photo != null) {
			photoIV.setImageBitmap(RotateBitmap(photo, 90));
		} else {
			Log.d(TAG, "photo is null");
			Log.d(TAG, "file path is : " + getIntent().getStringExtra(PostStatusActivity_PhotoPath));
		}
		
		//get location long lat:
		buildGoogleApiClient();
		
		postBtn.setOnClickListener(this);
	}
	
	public void postToWeb() {
		
		progressDialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		User user = (User) UserSingleton.getInstance().getObject();
		
		data.put("username", user.getUsername());
		data.put("placename", locationTV.getText().toString());
		data.put("lon", String.valueOf(this.longitude));
		data.put("lat", String.valueOf(this.latitude));
		data.put("status", this.postET.getText().toString());
		data.put("photo", encodeTobase64(photo));
		data.put("category", "Food");
		data.put("tag", "post");
		
		MobileApi.post(data, this);
	}
	
	@Override
	public void postResult(boolean success, String text) {
		//GO BACK TO PLACES ACTIVITY
		
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		
		finish();
	}
	
	public String encodeTobase64(Bitmap image)
	{
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    Log.d(TAG, "size of byte array: " + b.length);
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

//	    Log.e("LOOK", imageEncoded);
	    return imageEncoded;
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
				true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_status, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
		
		MobileApi.getFormattedAddress(longitude, latitude, this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			Toast.makeText(
					this,
					"Long: " + mLastLocation.getLongitude() + ", Lat: "
							+ mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
			this.latitude = mLastLocation.getLatitude();
			this.longitude = mLastLocation.getLongitude();
			
			MobileApi.getFormattedAddress(longitude, latitude, this);
		} else
			Toast.makeText(this, "No location located", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();

	}
	
	private Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=400;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        Bitmap b=  BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        Log.d(TAG, "Size of bitmap:  " + b.getByteCount());
	        Log.d(TAG, "Height: " + b.getHeight() + " width: " + b.getWidth());
	        return b;
	    } catch (FileNotFoundException e) {}
	    return null;
	}

	@Override
	public void getAddressResult(String address) {
		locationTV.setText(address);
	}

	@Override
	public void onClick(View v) {
		postToWeb();
	}
}