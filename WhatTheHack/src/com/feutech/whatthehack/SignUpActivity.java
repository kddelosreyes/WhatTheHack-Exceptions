package com.feutech.whatthehack;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.constants.Constants;
import com.feutech.whatthehack.listeners.RegisterListener;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.UserSingleton;

public class SignUpActivity extends Activity implements RegisterListener{
	EditText username;
	EditText pw1, pw2;
	EditText fname, lname;
	
	private ProgressDialog progressDialog;
	
	private static final int SHOW_REGISTRATION_ERROR = 1;
	private String errorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		username = (EditText)findViewById(R.id.username_et);
		pw1 = (EditText) findViewById(R.id.password_et);
		pw2 = (EditText) findViewById(R.id.verify_password_et);
		lname = (EditText) findViewById(R.id.lastname_et);
		fname = (EditText) findViewById(R.id.firstname_et);
		
	}
	
	public void signUp(View v) {
		String p1 = pw1.getText().toString();
		String p2 = pw2.getText().toString();
		if (!isPasswordMatching(p1, p2)) {
			Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
		} else if (!isFieldsFilled()){
			Toast.makeText(getApplicationContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
		} else {
			//continue and check online to register.
			
			HashMap<String, String> data = formatData();
			
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			MobileApi.registerUser(data, this);
		}
	}
	
	private HashMap<String, String> formatData(){
	
		HashMap <String, String> data = new HashMap<String, String>();

		data.put(Constants.API_REGISTER_FNAME, fname.getText().toString());
		data.put(Constants.API_REGISTER_LNAME, lname.getText().toString());
		data.put(Constants.API_REGISTER_USERNAME, username.getText().toString());
		data.put(Constants.API_REGISTER_PASSWORD, pw1.getText().toString());
		
		data.put("tag", Constants.TAG_REGISTER);
		return data;
	}

	public boolean isPasswordMatching(String p1, String p2) {
		if (p1.equals(p2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFieldsFilled(){
		String fnames = fname.getText().toString().trim();
		String lnames = lname.getText().toString().trim();
		String uname = username.getText().toString().trim();
		String pw = pw1.getText().toString().trim();
		if (fnames.isEmpty() || lnames.isEmpty() || uname.isEmpty() || pw.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void registerResult(boolean success, String text) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		
		if (success) {
			
			String firstName = fname.getText().toString();
			String lastName = lname.getText().toString();
			String uname = username.getText().toString();
			String password = pw1.getText().toString();
			
			UserSingleton.getInstance().setObject(new User(uname, password, lastName, firstName));
			
			Intent intent = new Intent(SignUpActivity.this, PlacesActivity.class);
			startActivity(intent);
			finish();
		} else {
			errorMessage = text;
			showDialog(SHOW_REGISTRATION_ERROR);
			
			Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
			startActivity(intent);
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
		case SHOW_REGISTRATION_ERROR:
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
		case SHOW_REGISTRATION_ERROR:
			textView.setText(errorMessage);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
		finish();
	}
}
