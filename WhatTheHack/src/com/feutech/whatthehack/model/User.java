package com.feutech.whatthehack.model;

public class User {

	private String username;
	private String password;
	private String lastName;
	private String firstName;
	
	public User () {
		
	}

	public User(String username, String password, String lastName,
			String firstName) {
		super();
		this.username = username;
		this.password = password;
		this.lastName = lastName;
		this.firstName = firstName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFistName() {
		return firstName;
	}

	public void setFistName(String fistName) {
		this.firstName = fistName;
	}	
}