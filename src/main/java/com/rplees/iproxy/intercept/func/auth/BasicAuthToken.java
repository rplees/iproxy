package com.rplees.iproxy.intercept.func.auth;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0
 * @created Apr 3, 2023 10:26:17 PM
 */
public class BasicAuthToken implements AuthToken {
	private String usr;
	private String pwd;

	public BasicAuthToken() {
	}

	public BasicAuthToken(String usr, String pwd) {
		this.usr = usr;
		this.pwd = pwd;
	}

	public String getUsr() {
		return usr;
	}

	public void setUsr(String usr) {
		this.usr = usr;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
