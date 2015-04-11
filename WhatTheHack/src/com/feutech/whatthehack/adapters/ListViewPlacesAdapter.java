package com.feutech.whatthehack.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.feutech.whatthehack.AppController;
import com.feutech.whatthehack.R;
import com.feutech.whatthehack.model.Place;

public class ListViewPlacesAdapter extends BaseAdapter{

	private Context context;
	
	private LayoutInflater inflater;
	
	private ImageLoader imageLoader;
	private ArrayList<Place> places;
	
	public ListViewPlacesAdapter(Context context, ArrayList<Place> places) {
		this.context = context;
		this.places = places;
	}
	
	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {
		
		if (inflater == null)
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		row = inflater.inflate(R.layout.list_item_places, parent, false);
		
		NetworkImageView networkImageViewPlace = (NetworkImageView) row.findViewById(R.id.networkImageViewPlace);
		TextView textViewPlace = (TextView) row.findViewById(R.id.textViewPlaces);
		
		if (imageLoader == null) {
			imageLoader = AppController.getInstance().getImageLoader();
		}
		
		networkImageViewPlace.setImageUrl(places.get(position).getPlace_picture(), imageLoader);
		textViewPlace.setText(places.get(position).getPlace_name());
		
		return row;
	}

}
