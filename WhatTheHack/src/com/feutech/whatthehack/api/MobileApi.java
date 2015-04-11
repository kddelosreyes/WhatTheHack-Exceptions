package com.feutech.whatthehack.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.feutech.whatthehack.AppController;
import com.feutech.whatthehack.constants.Constants;
import com.feutech.whatthehack.database.PlaceHelper;
import com.feutech.whatthehack.listeners.GetAddressListener;
import com.feutech.whatthehack.listeners.GetPlacesListener;
import com.feutech.whatthehack.listeners.GetPostListener;
import com.feutech.whatthehack.listeners.LoginListener;
import com.feutech.whatthehack.listeners.RegisterListener;
import com.feutech.whatthehack.model.Place;
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
//							Log.i("TAG", err.getMessage()); 
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
	
	public static void getPlaces(Context context, GetPlacesListener listener) {
		Thread t = new Thread(new ThreadGetPlaces(context, listener));
		t.run();
	}
	
	private static class ThreadGetPlaces implements Runnable {
		
		private Context context;
		private GetPlacesListener listener;
		
		public ThreadGetPlaces (Context context, GetPlacesListener listener) {
			this.context = context;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			
			StringRequest toPost = new StringRequest(Method.POST, Constants.BASE_URL, 
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.i("TAG", "places response: " + response);
							try {
								JSONObject jsonResponse = new JSONObject(response);
								
								if (jsonResponse.getInt("success") == 1) {
									
									JSONArray array = jsonResponse.getJSONArray("details");
									
									PlaceHelper helper = new PlaceHelper(context);
									helper.open();
									helper.deleteAll();
									helper.create();
									for (int x = 0; x < array.length(); x++) {
										JSONObject jsonObject = array.getJSONObject(x);
										
										String placeName = jsonObject.getString("place_name");
										String placePhoto = jsonObject.getString("photo");
										
										placePhoto = Constants.URL_PREFIX_PHOTO + "places/" + placePhoto.substring(placePhoto.lastIndexOf("/"));
										
										helper.insert(new Place(placeName, placePhoto));
									}
									
									helper.close();
									
									listener.getPlacesResult(true, "Success");
								} else
									listener.getPlacesResult(false, jsonResponse.getString("error_msg"));
							} catch (JSONException e) {
								e.printStackTrace();
								listener.getPlacesResult(false, e.getMessage());
							}
						}
					}, 
					
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError err) {
							err.printStackTrace();
							listener.getPlacesResult(false, err.getMessage());
						}
					})
			{
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("tag", Constants.TAG_PLACE);
					
					return data;
				}
			};
			
			AppController.getInstance().addToRequestQueue(toPost);
		}
	}
	
	public static void getPosts(GetPostListener listener) {
		Thread t = new Thread(new ThreadGetPosts(listener));
		t.run();
	}
	
	private static class ThreadGetPosts implements Runnable {

		private GetPostListener listener;
		
		public ThreadGetPosts(GetPostListener listener) {
			this.listener = listener;
		}
		
		@Override
		public void run() {
			StringRequest toPost = new StringRequest(Method.POST, Constants.BASE_URL,  
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.i("TAG", "response in getting posts: " + response);
				
							try {
								JSONObject responseObject = new JSONObject(response);
							} catch (JSONException e) {
								e.printStackTrace();
								listener.getPostResult(false, e.getMessage());
							}
						}
					},
					
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError err) {
							err.printStackTrace();
							listener.getPostResult(false, err.getMessage());
						}
					})
			{
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					HashMap<String, String> data = new HashMap<String, String>();
					
					data.put("tag", Constants.TAG_GET_POSTS);
					
					return data;
				}
			};
			
			AppController.getInstance().addToRequestQueue(toPost);
		}
	}
	
	public static void getFormattedAddress(double lon, double lat, GetAddressListener listener) {
		Thread t = new Thread(new ThreadGetFormattedAddress(lon, lat, listener));
		t.run();
	}
	
	private static class ThreadGetFormattedAddress implements Runnable {

		private double lon, lat;
		private GetAddressListener listener;
		
		public ThreadGetFormattedAddress(double lon, double lat, GetAddressListener listener) {
			this.lon = lon;
			this.lat = lat;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			StringRequest toPost = new StringRequest(Method.POST, Constants.BASE_URL, 
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							
							Log.i("TAG", "response in address: " + response);
							
							try {
								JSONObject jsonResponse = new JSONObject(response);
								
								listener.getAddressResult(jsonResponse.getString("address"));
							} catch (JSONException e) {
								e.printStackTrace();
								listener.getAddressResult("");
							}
						}
					},
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError err) {
							err.printStackTrace();
							listener.getAddressResult("");
						}
					})
			{
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					HashMap<String, String> data = new HashMap<String, String>();

					Log.i("TAG", "lon: " + lon + " lat: " + lat);
					
					data.put("lon", String.valueOf(lon));
					data.put("lat", String.valueOf(lat));
					data.put("tag", "address");
					
					return data;
				}
			};
			
			AppController.getInstance().addToRequestQueue(toPost);
		}
		
	}
}
