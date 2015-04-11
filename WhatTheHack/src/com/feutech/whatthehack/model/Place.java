package com.feutech.whatthehack.model;

public class Place {

	private String place_name;
	private String place_picture;
	
	public Place() {
		
	}

	public Place(String place_name, String place_picture) {
		super();
		this.place_name = place_name;
		this.place_picture = place_picture;
	}

	public String getPlace_name() {
		return place_name;
	}

	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}

	public String getPlace_picture() {
		return place_picture;
	}

	public void setPlace_picture(String place_picture) {
		this.place_picture = place_picture;
	}
}
