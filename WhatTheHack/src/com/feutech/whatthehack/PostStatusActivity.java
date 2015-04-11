package com.feutech.whatthehack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class PostStatusActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener{

	ImageView photoIV;
	GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;

	public static final String TAG = "com.feutech.whatthehack.PostStatusActivity";
	public static final String PostStatusActivity_PhotoPath = "com.feutech.whatthehack.PostStatusActivity.PhotoPath";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_status);

		photoIV = (ImageView) findViewById(R.id.PostActivity_imageIV);
		
		Bitmap photo = getScaledDrawable(this, getIntent().getStringExtra(PostStatusActivity_PhotoPath));

		if (photo != null) {
			photoIV.setImageBitmap(RotateBitmap(photo, 90));
		} else {
			Log.d(TAG, "photo is null");
			Log.d(TAG, "file path is : " + getIntent().getStringExtra(PostStatusActivity_PhotoPath));
		}
		
		buildGoogleApiClient();
	}

	@SuppressWarnings("deprecation")
	public static Bitmap getScaledDrawable(Activity a, String path) {
		Display display = a.getWindowManager().getDefaultDisplay();
		float destWidth = display.getWidth();
		float destHeight = display.getHeight();
		// Read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		int inSampleSize = 1;
		if (srcHeight > destHeight || srcWidth > destWidth) {
			if (srcWidth > srcHeight) {
				inSampleSize = Math.round(srcHeight / destHeight);
			} else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
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
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
        	Toast.makeText(this, "Long: " + mLastLocation.getLongitude() + ", Lat: " + mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No location located", Toast.LENGTH_LONG).show();
        }
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
		
	}
}