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
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener,
		LoginListener {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private ProgressDialog progressDialog;

	private static final String EMPTY = "";
	private static final String WARNING_MESSAGE = "Username and/or password empty!";
	private static final String DOES_NOT_EXIST = "Please check your Internet connection.";

	private static final int SHOW_ERROR_DIALOG = 1;
	private static final int SHOW_LOGIN_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initComponents();
	}

	private void initComponents() {
		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
	}

	private boolean isEmpty(View view) {
		String viewValue = ((EditText) view).getText().toString();
		if (viewValue.equals(EMPTY))
			return true;
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogview = null;

		switch (id) {
		case SHOW_ERROR_DIALOG:
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
		case SHOW_ERROR_DIALOG:
			textView.setText(WARNING_MESSAGE);
		case SHOW_LOGIN_ERROR:
			textView.setText(DOES_NOT_EXIST);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginButton:
			if (isEmpty(usernameEditText) || isEmpty(passwordEditText)) {
				showDialog(SHOW_ERROR_DIALOG);
			} else {
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("Logging in...");
				progressDialog.setCancelable(false);
				progressDialog.show();

				MobileApi.loginUser(username, password, this);
			}
			break;
		case R.id.registerButton:
			Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
			startActivity(intent);
			break;
		case R.id.needHelpButton:
			break;
		}
	}

	@Override
	public void loginResult(boolean success, String text) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();

		if (success) {
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("_username", username);
			editor.putString("_password", password);
			editor.putBoolean("_hasLogged", true);
			editor.commit();
			
			passwordEditText.setText("");
			Intent intent = new Intent(LoginActivity.this, PlacesActivity.class);
			startActivity(intent);
			finish();
		} else {
			showDialog(SHOW_LOGIN_ERROR);
		}
	}
}
