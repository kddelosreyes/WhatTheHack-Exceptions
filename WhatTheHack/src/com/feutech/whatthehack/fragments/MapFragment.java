package com.feutech.whatthehack.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.feutech.whatthehack.AppController;
import com.feutech.whatthehack.R;
import com.feutech.whatthehack.api.MobileApi;
import com.feutech.whatthehack.database.PostHelper;
import com.feutech.whatthehack.listeners.PerformVoteListener;
import com.feutech.whatthehack.model.Post;
import com.feutech.whatthehack.model.User;
import com.feutech.whatthehack.utilities.DirectionsJSONParser;
import com.feutech.whatthehack.utilities.UserSingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapFragment extends Fragment implements PerformVoteListener{

	private ImageLoader imageLoader;
	
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	
	private View view;
	
	private LatLng latlngPosition;
	
	private User user;
	
	private ArrayList<Post> listOfPosts = new ArrayList<Post>();
	
	private Hashtable<String, Marker> markerPoints;
	private Hashtable<String, Post> markers;
	
	public MapFragment(LatLng latlngPosition) {
		this.latlngPosition = latlngPosition;
		
		imageLoader = AppController.getInstance().getImageLoader();
		
		user = (User) UserSingleton.getInstance().getObject();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	
		view = inflater.inflate(R.layout.fragment_map_holder, container, false);
		
		PostHelper helper = new PostHelper(getActivity());
		helper.open();
		listOfPosts = helper.getAllPosts();
		helper.close();
		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentManager fm = getChildFragmentManager();
		mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		
		if (mapFragment == null) {
			mapFragment = SupportMapFragment.newInstance();
	        fm.beginTransaction().replace(R.id.map, mapFragment).commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (map == null)
			map = mapFragment.getMap();

		markLocations();
	}

	private void markLocations() {
		markers = new Hashtable<String, Post>();
		markerPoints = new Hashtable<String, Marker>();
		
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlngPosition).zoom(16).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        
        MarkerOptions marker = new MarkerOptions().position(latlngPosition).title("Im here");
        
        map.addMarker(marker);
        
        initInfoWindow();
        
        for (Post post : listOfPosts) {
        	drawMarker(post);
        }
	}

	private void drawMarker(Post post) {
		MarkerOptions markerOptions = new MarkerOptions();
		
		markerOptions.position(new LatLng(post.getLatitude(), post.getLongitude()));
		markerOptions.title(post.getUsername());
		
		Marker marker = map.addMarker(markerOptions);
		markerPoints.put(marker.getId(), marker);
		markers.put(marker.getId(), post);
	}
	
	private void initInfoWindow() {
		
		map.setOnMarkerClickListener(onMarkerClicked);
		
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			View v;
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				
				v = getActivity().getLayoutInflater().inflate(R.layout.list_item_posts, null);
				
				NetworkImageView networkImageViewPlace = (NetworkImageView) v.findViewById(R.id.networkImageViewPlace);
				
				TextView textViewUsername = (TextView) v.findViewById(R.id.textViewUsername);
				TextView textViewAddress = (TextView) v.findViewById(R.id.textViewAddress);
				TextView textViewStatusMessage = (TextView) v.findViewById(R.id.textViewStatusMessage);
				
				ImageView imageViewCategory = (ImageView) v.findViewById(R.id.imageViewCategory);
				TextView downVoteCounter = (TextView) v.findViewById(R.id.downvoteCounter);
				ImageView downVoteCategory = (ImageView) v.findViewById(R.id.downvoteCategory);
				TextView upVoteCounter = (TextView) v.findViewById(R.id.upvoteCounter);
				ImageView upVoteCategory = (ImageView) v.findViewById(R.id.upvoteCategory);
				
				final Post post = markers.get(marker.getId());
				
				networkImageViewPlace.setImageUrl(post.getPhoto(), imageLoader);
				
				textViewUsername.setText(post.getUsername());
				textViewAddress.setText(post.getPlacename());
				textViewStatusMessage.setText(post.getStatusMessage());
				
				int upVoteFlag = post.getUpvote();
				int downVoteFlag = post.getDownvote();
				
				upVoteCategory.setImageResource(upVoteFlag == 1 ? R.drawable.voted_up : R.drawable.vote_up);
				downVoteCategory.setImageResource(downVoteFlag == 1 ? R.drawable.voted_down : R.drawable.vote_down);
				
				upVoteCounter.setText(String.valueOf(post.getTotalUpvote()));
				downVoteCounter.setText(String.valueOf(post.getTotaldownVote()));
				
				upVoteCategory.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MobileApi.votePost(user.getUsername(), post.getPost_id(), "upvote", MapFragment.this);
					}
				});
				
				downVoteCategory.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MobileApi.votePost(user.getUsername(), post.getPost_id(), "downvote", MapFragment.this);
					}
				});
				
				Log.i("TAG", marker.getId() + " : " + post.getStatusMessage());
				return v;
			}
		});
	}
	
	OnMarkerClickListener onMarkerClicked = new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {
	
			Log.i("TAG", marker.getId());
			
			if (marker.getId().equals("m0"))
				return true;
			
//			map.addpo
			
			Post post = markers.get(marker.getId());
			LatLng latlngPost = new LatLng(post.getLatitude(), post.getLongitude());
			
			String url = getDirectionsUrl(latlngPosition, latlngPost);
	         
            DownloadTask downloadTask = new DownloadTask();
            
            downloadTask.execute(url);
            
			marker.showInfoWindow();
			return true;
		}
	};

	@Override
	public void voteResultPerformed(String text, boolean success, String vote,
			int post_id) {
		if (success) {
			updateList(vote, post_id);
		} else {}
	}
	
	private void updateList(String vote, int post_id) {
		PostHelper helper = new PostHelper(getActivity());
		helper.open();
		helper.updateVotes(post_id, vote);
		
		listOfPosts = helper.getAllPosts();
		helper.close();
	}
	
	private String getDirectionsUrl(LatLng origin, LatLng dest)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
 
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
 
        return url;
    }
 
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        } catch (Exception e)
        {
            Log.d("Exception while downloading url", e.toString());
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
 
            // For storing data from web service
            String data = "";
 
            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
 
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if (result.size() < 1)
            {
                Toast.makeText(getActivity(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
 
                    if (j == 0)
                    { // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1)
                    { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(3);
                lineOptions.color(Color.RED);
            }
 
            map.addPolyline(lineOptions);
        }
    }
}