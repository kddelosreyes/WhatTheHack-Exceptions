package com.feutech.whatthehack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.fragments.MapPlacesFragment;
import com.feutech.whatthehack.fragments.PostsListViewFragment;
import com.feutech.whatthehack.listeners.GetPostListener;
import com.feutech.whatthehack.utilities.ConnectionChecker;

public class LandingFragmentActivity extends FragmentActivity implements OnClickListener, GetPostListener{

	private FragmentManager fm;
	
	private ViewPager viewPager;
	
	private ImageView imageViewMap, imageViewSearch;
	
	public static final String PLACE_NAME = "placename";
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.landing_fragment_activity);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent intent = getIntent();
		String placeName = intent.getStringExtra(PLACE_NAME);
		getActionBar().setTitle(placeName);
		
		fm = getSupportFragmentManager();

		imageViewMap = (ImageView) findViewById(R.id.imageViewMap);
		imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
		
		imageViewMap.setOnClickListener(this);
		imageViewSearch.setOnClickListener(this);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new SlidePagerAdapter(fm));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		if (ConnectionChecker.isNetworkAvailable(this)) {
//			progressDialog = new ProgressDialog(this);
//			progressDialog.setMessage("Loading...");
//			progressDialog.setCancelable(false);
//			progressDialog.show();
//			
//			MobileApi.getPosts(this);
//		} else {
//			//DISPLAY NO CONNECTION MESSAGE
//		}
		viewPager.setCurrentItem(0);
	}
	
	@Override
	public void getPostResult(boolean success, String text) {
		
//		if (progressDialog.isShowing())
//			progressDialog.dismiss();
//		
//		if (success) {
//			viewPager = (ViewPager) findViewById(R.id.pager);
//			viewPager.setAdapter(new SlidePagerAdapter(fm));
//		} else {
//			// DISPLAY ERROR MESSAGE ON TEXT
//		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imageViewSearch:
				viewPager.setCurrentItem(1);
				break;
			case R.id.imageViewMap:
				viewPager.setCurrentItem(0);
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
				return new MapPlacesFragment();
			else
				return new PostsListViewFragment();
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}