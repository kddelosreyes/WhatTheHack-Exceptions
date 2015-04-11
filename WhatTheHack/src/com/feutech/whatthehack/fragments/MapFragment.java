package com.feutech.whatthehack.fragments;

import GPS.GPSTracker;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.feutech.whatthehack.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment{

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private GPSTracker gps;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	
		view = inflater.inflate(R.layout.fragment_map_holder, container, false);
		
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
		
		gps = new GPSTracker(getActivity());
		if(gps.canGetLocation()){
			double latitude;
        	double longitude;
			
        	latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setZoomControlsEnabled(true);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(16).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Im here");
            map.addMarker(marker);
		}
	}
}
