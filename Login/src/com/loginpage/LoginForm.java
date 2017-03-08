package com.loginpage;

import java.io.Serializable;

public class LoginForm implements Serializable {
	private String loginName;
	private String password;
	private String rememberMyPassword;
	public LoginForm() {
		loginName = "";
		password = "";
		rememberMyPassword = null;
	}
	
	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName.trim();
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRememberMyPassword() {
		return rememberMyPassword;
	}
	public void setRememberMyPassword(String rememberMyPassword) {
		this.rememberMyPassword = rememberMyPassword;
	}
}
