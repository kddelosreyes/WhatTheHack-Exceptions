package com.feutech.whatthehack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	EditText username;
	EditText pw1, pw2;
	EditText fname, lname;

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
			
			//temporarily go to placesActivity first
			Intent intent = new Intent(SignUpActivity.this, PlacesActivity.class);
			startActivity(intent);
		}
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
}
