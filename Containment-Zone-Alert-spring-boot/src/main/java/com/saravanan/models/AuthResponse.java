package com.saravanan.models;

public class AuthResponse {
	String jwt;

	public AuthResponse() {
	}

	public AuthResponse(String jwt) {
		super();
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
    
	public String toString() {
		return this.jwt;
	}
}
