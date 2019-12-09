package com.packtpub.liverestaurant;

public class AutenticaResponse {
	private String token;
	public AutenticaResponse() {
		
	}
	
	public void setToken(String token) {
		this.token= token;
	}
	
	public String getToken() {
		return this.token;
	}
}