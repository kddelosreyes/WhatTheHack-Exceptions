package com.feutech.whatthehack;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlacesActivity extends Activity {

	ListView placesListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		
		
		ArrayList<Place> places = new ArrayList<Place>();
		
		//START for testing only
		places.add(new Place("Manila"));
		places.add(new Place("Makati"));
		places.add(new Place("Quezon City"));
		//END for testing only
		
		PlacesAdapter placesAdapter = new PlacesAdapter(this, places);
		placesListView = (ListView) findViewById(R.id.places_listView);
		placesListView.setAdapter(placesAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.places, menu);
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
}

class Place {
	String name;
	public Place (String ucName) {
		this.name = ucName;
	}
} 

class PlacesAdapter extends ArrayAdapter<Place> {
	Activity context;
	List<Place> places = new ArrayList<Place>();
	public PlacesAdapter(Activity context, List<Place> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
		this.places = objects;	
		this.context = context;
	}
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.list_item_places, null);
		
		//This is once we get the background from the web
		//convertView.setBackground(background);
		
		TextView placeName = (TextView) convertView.findViewById(R.id.places_name_tv);
		placeName.setText(places.get(position).name);		
		
		return convertView;
	}
	
	
}
