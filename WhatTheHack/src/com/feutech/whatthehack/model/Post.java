package com.feutech.whatthehack.model;

public class Post {
	private int post_id;
	private String username;
	private double longitude;
	private double latitude;
	private String remarks;
	private String photo;
	private String category;
	private String place_name;
	
	public Post () {
		
	}
	
	public Post(int post_id, String username, double longitude,
			double latitude, String remarks, String photo, String category,
			String place_name) {
		super();
		this.post_id = post_id;
		this.username = username;
		this.longitude = longitude;
		this.latitude = latitude;
		this.remarks = remarks;
		this.photo = photo;
		this.category = category;
		this.place_name = place_name;
	}

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPlace_name() {
		return place_name;
	}

	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}
}
