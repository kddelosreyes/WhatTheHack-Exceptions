package com.feutech.whatthehack.model;

public class Post {
	private int post_id;
	private String username;
	private String placename;
	private double latitude;
	private double longitude;
	private String statusMessage;
	private String photo;
	private String category;
	private String datePosted;
	private int upvote;
	private int downvote;
	private int totalUpvote;
	private int totaldownVote;
	
	public Post () {
		
	}

	public Post(int post_id, String username, String placename,
			double latitude, double longitude, String statusMessage,
			String photo, String category, String datePosted, int upvote,
			int downvote, int totalUpvote, int totaldownVote) {
		super();
		this.post_id = post_id;
		this.username = username;
		this.placename = placename;
		this.latitude = latitude;
		this.longitude = longitude;
		this.statusMessage = statusMessage;
		this.photo = photo;
		this.category = category;
		this.datePosted = datePosted;
		this.upvote = upvote;
		this.downvote = downvote;
		this.totalUpvote = totalUpvote;
		this.totaldownVote = totaldownVote;
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

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
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

	public String getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(String datePosted) {
		this.datePosted = datePosted;
	}

	public int getUpvote() {
		return upvote;
	}

	public void setUpvote(int upvote) {
		this.upvote = upvote;
	}

	public int getDownvote() {
		return downvote;
	}

	public void setDownvote(int downvote) {
		this.downvote = downvote;
	}

	public int getTotalUpvote() {
		return totalUpvote;
	}

	public void setTotalUpvote(int totalUpvote) {
		this.totalUpvote = totalUpvote;
	}

	public int getTotaldownVote() {
		return totaldownVote;
	}

	public void setTotaldownVote(int totaldownVote) {
		this.totaldownVote = totaldownVote;
	}
}
