package com.feutech.whatthehack;

import GPS.GPSTracker;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.fragments.MapPlacesFragment;
import com.feutech.whatthehack.fragments.PostsListViewFragment;
import com.feutech.whatthehack.listeners.GetPostListener;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.ConnectionChecker;
import com.feutech.whatthehack.utilities.UserSingleton;
import com.google.android.gms.maps.model.LatLng;

public class LandingFragmentActivity extends FragmentActivity implements OnClickListener, GetPostListener{

	private FragmentManager fm;
	
	private ViewPager viewPager;
	
	private ImageView imageViewMap, imageViewSearch;
	
	public static final String PLACE_NAME = "placename";
	
	private ProgressDialog progressDialog;
	
	private String placeName;
	private boolean isWhatsNearToMe;
	
	private GPSTracker gps;
	
	private double lat, lng;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.landing_fragment_activity);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent intent = getIntent();
		placeName = intent.getStringExtra(PLACE_NAME);
		isWhatsNearToMe = intent.getBooleanExtra("whatsNearToMe", true);
		
		getActionBar().setTitle(placeName);
		
		fm = getSupportFragmentManager();

		imageViewMap = (ImageView) findViewById(R.id.imageViewMap);
		imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
		
		imageViewMap.setOnClickListener(this);
		imageViewSearch.setOnClickListener(this);
		
		if (isWhatsNearToMe) {
			//WHAT'S NEAR TO ME
			gps = new GPSTracker(this);
			
			if (gps.canGetLocation()) {
				lat = gps.getLatitude();
				lng = gps.getLongitude();
			} else {
				Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
				lat = 0;
				lng = 0;
			}
			
		} else {
			//OTHER PLACES
			lat = intent.getDoubleExtra("lat", 0);
			lng = intent.getDoubleExtra("lon", 0);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (ConnectionChecker.isNetworkAvailable(this)) {
			
			User user = (User) UserSingleton.getInstance().getObject();
			
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			
			MobileApi.getPosts(this, user.getUsername(), lat, lng, this);
		} else {
			//DISPLAY NO CONNECTION MESSAGE
		}
	}
	
	@Override
	public void getPostResult(boolean success, String text) {
		
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		
		if (success) {
			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(new SlidePagerAdapter(fm));
			viewPager.setCurrentItem(0);
		} else {
			// DISPLAY ERROR MESSAGE ON TEXT
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imageViewSearch:
				if (viewPager != null)
					viewPager.setCurrentItem(1);
				else
					Toast.makeText(this, "Error in network/internal operations. Please try again.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.imageViewMap:
				if (viewPager != null)
					viewPager.setCurrentItem(0);
				else
					Toast.makeText(this, "Error in network/internal operations. Please try again.", Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	private class SlidePagerAdapter extends FragmentPagerAdapter {

		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0)
				return new MapPlacesFragment(new LatLng(lat, lng));
			else
				return new PostsListViewFragment();
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}