package com.feutech.whatthehack;

import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.listeners.LoginListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity implements LoginListener {

	private final int TIME = 3000;
	private ProgressDialog progressDialog;
	private SharedPreferences sharedPreferences;
	
	private static final int SHOW_LOGIN_ERROR = 2;
	
	private static final String DOES_NOT_EXIST = "User does not exists.";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
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
		boolean hasLogged = sharedPreferences.getBoolean("_hasLogged", false);
		if(hasLogged) {
			String username = sharedPreferences.getString("_username", "");
			String password = sharedPreferences.getString("_password", "");
			
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Logging in...");
			progressDialog.setCancelable(false);
			progressDialog.show();

			MobileApi.loginUser(username, password, this);
		} else {
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogview = null;

		switch (id) {
		case SHOW_LOGIN_ERROR:
			dialogview = inflater.inflate(R.layout.alert_dialog_warning, null);
			break;
		}

		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogbuilder.setView(dialogview);
		dialogDetails = dialogbuilder.create();

		return dialogDetails;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		final AlertDialog alertDialog = (AlertDialog) dialog;
		final TextView textView = (TextView) alertDialog
				.findViewById(R.id.warningTextView);

		switch (id) {
		case SHOW_LOGIN_ERROR:
			textView.setText(DOES_NOT_EXIST);
			break;
		}
	}

	@Override
	public void loginResult(boolean success, String text) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();

		if (success) {			
			Intent intent = new Intent(SplashActivity.this, PlacesActivity.class);
			startActivity(intent);
			finish();
		} else {
			showDialog(SHOW_LOGIN_ERROR);
		}
	}
}