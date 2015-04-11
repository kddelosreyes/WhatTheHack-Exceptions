package com.feutech.whatthehack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	private final int TIME = 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				fetchData();
			}
		}, TIME);
	}
	
	private void fetchData() {
		startActivity(new Intent(SplashActivity.this, MainActivity.class));
		finish();
	}
}