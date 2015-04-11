package com.feutech.whatthehack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.feutech.whatthehack.fragments.MapPlacesFragment;

public class LandingFragmentActivity extends FragmentActivity{

	private FragmentManager fm;
	
	private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.landing_fragment_activity);
		
		fm = getSupportFragmentManager();
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		viewPager.setAdapter(new SlidePagerAdapter(fm));
	}
	
	private class SlidePagerAdapter extends FragmentPagerAdapter {

		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new MapPlacesFragment();
		}

		@Override
		public int getCount() {
			return 1;
		}
		
	}
}
