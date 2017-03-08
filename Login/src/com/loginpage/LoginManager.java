package com.loginpage;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class LoginManager {
	private final static String LOGIN_NAME_SESSION_ATTRIBUTE ="loginName";
	private static final String LOGIN_NAME_COOKIE = "loginName";
	private static final String PASSWORD_COOKIE = "password";
	private static final int COOKIES_TIME_TO_LIVE_REMEMBER_MY_PASSWORD =30 * 24 * 3600; // 30 days
	private static final int COOKIES_TIME_TO_LIVE_REMOVE = 0;
	
	private LoginManager() {}
	
	public final static void login(HttpServletRequest request,HttpServletResponse response, String loginName,String password, boolean rememberMyPassword)throws IncorrectPasswordException, IOException {
		PrintWriter out = response.getWriter();
		out.println(loginName);
		validateLogin(loginName, password);
		HttpSession session = request.getSession(true);	
		session.setAttribute(LOGIN_NAME_SESSION_ATTRIBUTE, loginName);
		if (rememberMyPassword) {
			leaveCookies(response, loginName, password,COOKIES_TIME_TO_LIVE_REMEMBER_MY_PASSWORD);
		}
	}
	
	public final static void logout(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		leaveCookies(response, "", "", COOKIES_TIME_TO_LIVE_REMOVE);
	}

	public final static String getLoginName(HttpServletRequest request) {
		/* Try to get login name from session. */
		String loginName = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			loginName = (String) session.getAttribute(LOGIN_NAME_SESSION_ATTRIBUTE);
			if (loginName != null) {
				return loginName;
			}
		}
		/*
		* The user had not logged in or his/her session has expired.
		* We need to check if the user has selected "remember my
		* password" in the last login (login name and password will
		* come as cookies). If so, we save login name in the session.
		*/
		loginName = getLoginNameFromCookies(request);
		if (loginName != null) {
			session = request.getSession(true);
			session.setAttribute(LOGIN_NAME_SESSION_ATTRIBUTE,loginName);
		}
		return loginName;
		}
	
	private final static void validateLogin(String loginName,String password) throws IncorrectPasswordException {
		if (!loginName.equals(password)) {
			throw new IncorrectPasswordException();
		}
	}
	
	private final static void leaveCookies(HttpServletResponse response, String loginName,String password, int timeToLive){
		/* Create cookies. */	
		Cookie loginNameCookie = new Cookie(LOGIN_NAME_COOKIE,loginName);
		Cookie passwordCookie = new Cookie(PASSWORD_COOKIE, password);
		/* Set maximum age to cookies. */
		loginNameCookie.setMaxAge(timeToLive);
		passwordCookie.setMaxAge(timeToLive);
		/* Add cookies to response. */
		response.addCookie(loginNameCookie);
		response.addCookie(passwordCookie);
		}
	
	private final static String getLoginNameFromCookies(HttpServletRequest request) {
		/* Are there cookies in the request ? */
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		/* Check if login name and password come as cookies. */
		String loginName = null;
		String password = null;
		int foundCookies = 0;
		for (int i=0; (i<cookies.length) && (foundCookies < 2); i++) {
			if (cookies[i].getName().equals(LOGIN_NAME_COOKIE)) {
				loginName = cookies[i].getValue();
				foundCookies++;
			}
			if (cookies[i].getName().equals(PASSWORD_COOKIE)) {
				password = cookies[i].getValue();
				foundCookies++;
			}
		}
		if (foundCookies != 2) {
			return null;
		}
		/* Validate loginName and password. */
		try {
			validateLogin(loginName, password);
			return loginName;
		} catch (IncorrectPasswordException e) {
			return null;
		}
	}	
}
