package com.feutech.whatthehack;

import static java.lang.Double.parseDouble;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private EditText usernameEditText;
	private EditText passwordEditText;
	
	private static final String EMPTY = "";
	private static final String WARNING_MESSAGE = "Username and/or password empty!";
	
	private static final int SHOW_ERROR_DIALOG = 1; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initComponents();
	}
	
	private void initComponents() {
		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
	}
	
	private boolean isEmpty(View view) {
		String viewValue = ((EditText) view).getText().toString();
		if(viewValue.equals(EMPTY)) return true;
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogview = null;

		switch (id) {
		case SHOW_ERROR_DIALOG:
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
		case SHOW_ERROR_DIALOG:
			textView.setText(WARNING_MESSAGE);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.loginButton:
			if(isEmpty(usernameEditText) || isEmpty(passwordEditText)) {
				showDialog(SHOW_ERROR_DIALOG);
			}
			break;
		case R.id.registerButton:
			Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
			startActivity(intent);
			break;
		case R.id.needHelpButton:
			break;
		}
	}
}
