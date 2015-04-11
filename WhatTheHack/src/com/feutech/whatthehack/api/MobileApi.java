package com.feutech.whatthehack.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.feutech.whatthehack.AppController;
import com.feutech.whatthehack.constants.Constants;
import com.feutech.whatthehack.listeners.GetPostListener;
import com.feutech.whatthehack.listeners.LoginListener;
import com.feutech.whatthehack.listeners.RegisterListener;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.UserSingleton;

public class MobileApi {

	public static void loginUser(String userName, String password, LoginListener listener) {
		Thread t = new Thread(new ThreadLoginUser(userName, password, listener));
		t.run();
	}
	
	private static class ThreadLoginUser implements Runnable {

		private String username;
		private String password;
		
		private LoginListener listener;
		
		public ThreadLoginUser(String userName, String password, LoginListener listener) {
			this.username = userName;
			this.password = password;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			StringRequest toPost = new StringRequest(Method.POST, Constants.BASE_URL,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.i("TAG", "login response: " + response);
							
							try {
								JSONObject responseObject = new JSONObject(response);
								
								if (responseObject.getString("success").equals("1")) {
									String firstName = responseObject.getString(Constants.API_LOGIN_RESPONSE_FIRST_NAME);
									String lastName = responseObject.getString(Constants.API_LOGIN_RESPONSE_LAST_NAME);
									String username = responseObject.getString(Constants.API_LOGIN_RESPONSE_USERNAME);
									String password = responseObject.getString(Constants.API_LOGIN_RESPONSE_PASSWORD);
									
									UserSingleton.getInstance().setObject(
											new User(username, password, lastName,
													firstName));
									
									listener.loginResult(true, "success");
								} else
									listener.loginResult(false, Constants.DOES_NOT_EXIST);
								
							} catch (JSONException e) {
								e.printStackTrace();
								listener.loginResult(false, e.getMessage());
							}
						}
					}, 
					
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError err) {
							err.printStackTrace();
							Log.i("TAG", err.getMessage());
							listener.loginResult(false, err.getMessage());
						}
					})
			{
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					
					HashMap<String, String> data = new HashMap<String, String>();
					
					data.put(Constants.API_LOGIN_USERNAME, username);
					data.put(Constants.API_LOGIN_PASSWORD, password);
					data.put("tag", Constants.TAG_LOGIN);
					
					return data;
				}
			};

			AppController.getInstance().addToRequestQueue(toPost);
		}
	}
	
	public static void registerUser(HashMap<String, String> data, RegisterListener listener) {
		Thread t = new Thread(new ThreadRegisterUser(data, listener));
		t.run();
	}
	
	private static class ThreadRegisterUser implements Runnable {

		private HashMap<String, String> data;
		
		private RegisterListener listener;
		
		public ThreadRegisterUser(HashMap<String, String> data, RegisterListener listener) {
			this.data = data;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			StringRequest toPost = new StringRequest(Method.POST, Constants.BASE_URL, 
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.i("TAG", "register response: " + response);
							listener.registerResult(true, response);
						}
					}, 
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError err) {
							err.printStackTrace();
							Log.i("TAG", err.getMessage());
							listener.registerResult(false, err.getMessage());
						}
					})
			{
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					return data;
				}
			};
			
			AppController.getInstance().addToRequestQueue(toPost);
		}
	}
	
	public static void getPosts(GetPostListener listener) {
		
	}
}