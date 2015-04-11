package com.feutech.whatthehack.fragments;

import com.feutech.whatthehack.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment{

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	
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
	}
}
