package com.feutech.whatthehack;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		username = (EditText)findViewById(R.id.username_et);
		pw1 = (EditText) findViewById(R.id.password_et);
		pw2 = (EditText) findViewById(R.id.verify_password_et);
		lname = (EditText) findViewById(R.id.lastname_et);
		fname = (EditText) findViewById(R.id.firstname_et);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
	
	public void signUp() {
		String p1 = pw1.getText().toString();
		String p2 = pw2.getText().toString();
		if (!isPasswordMatching(p1, p2)) {
			Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
		} else if (!isFieldsFilled()){
			Toast.makeText(getApplicationContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
		} else {
			//continue and check online to register.
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
