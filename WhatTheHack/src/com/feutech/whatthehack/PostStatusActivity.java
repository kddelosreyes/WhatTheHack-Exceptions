package com.feutech.whatthehack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class PostStatusActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	// comment
	private ImageView photoIV;
	private TextView locationTV;
	GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	private Object mContentResolver;
	
	private double longitude, latitude;
	private boolean locationFound = false;

	public static final String TAG = "com.feutech.whatthehack.PostStatusActivity";
	public static final String PostStatusActivity_PhotoPath = "com.feutech.whatthehack.PostStatusActivity.PhotoPath";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_status);

		photoIV = (ImageView) findViewById(R.id.PostActivity_imageIV);
		locationTV = (TextView) findViewById(R.id.PostStatus_location_TextView);
		
		String photoPath = getIntent().getStringExtra(PostStatusActivity_PhotoPath).trim();

		Bitmap photo = getBitmap(photoPath);

		//set photo to screen
		if (photo != null) {
			photoIV.setImageBitmap(RotateBitmap(photo, 90));
		} else {
			Log.d(TAG, "photo is null");
			Log.d(TAG, "file path is : " + getIntent().getStringExtra(PostStatusActivity_PhotoPath));
		}
		
		//get location long lat:
		buildGoogleApiClient();
		
		//resolve location to address:
		String address = resolveAddress(this.latitude, this.longitude);
		Toast.makeText(getApplicationContext(), "Resolved address: " + address, Toast.LENGTH_SHORT).show();
		if (!address.isEmpty())
		{
			locationTV.setText(address);
		}
	}
	
	public String resolveAddress(double latitude, double longitude) {
		String filterAddress = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

			Log.d(TAG, "Addresses: " + addresses.toString());
			if (addresses.size() > 0) {
                for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
                    filterAddress += addresses.get(0).getAddressLine(index) + " ";
            }
			
			
		} catch (IOException ex) {
			Log.d(TAG, ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e2) {
			// TODO: handle exception
			Log.d(TAG, e2.getMessage());
			e2.printStackTrace();
		}
		return filterAddress;
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
			this.locationFound = true;
			
		} else {
			Toast.makeText(this, "No location located", Toast.LENGTH_LONG).show();
			this.locationFound = false;
		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();

	}

	private Bitmap getBitmap(String path) {
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
			in = new FileInputStream(path);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: "
					+ o.outHeight);

			Bitmap b = null;
			
			in = new FileInputStream(path);

			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: "
						+ height);

				double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();

			Log.d(TAG, "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
			return b;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}
}
