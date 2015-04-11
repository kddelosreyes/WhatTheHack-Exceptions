package com.feutech.whatthehack;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.feutech.whatthehack.adapters.ListViewPlacesAdapter;
import com.feutech.whatthehack.database.PlaceHelper;
import com.feutech.whatthehack.model.Place;

public class PlacesActivity extends Activity implements OnItemClickListener{

	private ListView placesListView;
	private ArrayList<Place> places;
	
	private ListViewPlacesAdapter adapter;
	
	private static final long delay = 2000L;
    private boolean mRecentlyBackPressed = false;
    private Handler mExitHandler = new Handler();
    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mRecentlyBackPressed=false;   
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		places = new ArrayList<Place>();

		placesListView = (ListView) findViewById(R.id.places_listView);
		
		places = getPlacesFromDB();
		
		adapter = new ListViewPlacesAdapter(this, places);
		
		placesListView.setAdapter(adapter);
		placesListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
	}

	private ArrayList<Place> getPlacesFromDB() {
		
		PlaceHelper helper = new PlaceHelper(this);
		helper.open();
		helper.create();
		ArrayList<Place> places = helper.getAllPlaces();
		helper.close();
		
		return places;
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
		if (id == R.id.action_logout) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			
			editor.putString("_username", "");
			editor.putString("_password", "");
			editor.putBoolean("_hasLogged", false);
			editor.commit();
			
			startActivity(new Intent(PlacesActivity.this, LoginActivity.class));
			finish();

			Intent intent = new Intent(PlacesActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public void onBackPressed() {
        if (mRecentlyBackPressed) {
            mExitHandler.removeCallbacks(mExitRunnable);
            mExitHandler = null;
            super.onBackPressed();
            finish();
        } else {
            mRecentlyBackPressed = true;
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
            mExitHandler.postDelayed(mExitRunnable, delay);
        }
    }
}
