package com.feutech.whatthehack.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feutech.whatthehack.R;

public class MapPlacesFragment extends Fragment {

	private View view;
	
	private MapFragment mapFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_map_view, container, false);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initializeMap();
	}
	
	private void initializeMap() {
		mapFragment = new MapFragment();
		getFragmentManager().beginTransaction()
				.replace(R.id.mapPlaceholder, mapFragment).commit();
	}
}
