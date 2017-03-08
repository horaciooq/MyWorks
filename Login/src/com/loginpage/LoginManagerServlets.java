package com.loginpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginManagerServlets {
	private final static String LOGIN_NAME_SESSION_ATTRIBUTE ="loginName";
	
	private LoginManagerServlets() {}
	
	public final static void login(HttpServletRequest request,String loginName) {
		HttpSession session = request.getSession(true);
		session.setAttribute(LOGIN_NAME_SESSION_ATTRIBUTE, loginName);
	}
	
	public final static void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
	
	public final static String getLoginName(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		} else {
			return (String) session.getAttribute(LOGIN_NAME_SESSION_ATTRIBUTE);
		}
	}

}
