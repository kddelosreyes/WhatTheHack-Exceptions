package com.feutech.whatthehack;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class LandingFragmentActivity extends FragmentActivity{

	private FragmentManager fm;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	
		fm = getSupportFragmentManager();
		
	}
}
