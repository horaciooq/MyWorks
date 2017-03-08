<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.Map, java.util.HashMap,
	com.loginpage.LoginForm,
	com.loginpage.LoginManager,
	com.loginpage.IncorrectPasswordException"
%>
<jsp:useBean id="loginForm" scope="request" class="com.loginpage.LoginForm" />
<jsp:setProperty name="loginForm" property="*"/>
<%-- Mandatory parameters are present ? --%>
<%
	String loginName = loginForm.getLoginName();
	String password = loginForm.getPassword();
	Map errors = new HashMap();
	if ( (loginName == null) || (loginName.length() == 0) ) {
	errors.put("loginName", "Mandatory field");
	}
	if ( (password == null) || (password.length() == 0) ) {
	errors.put("password", "Mandatory field");
	}
	if (!errors.isEmpty()) {
	request.setAttribute("errors", errors);
	%>
<jsp:forward page="ShowLogin.jsp" />
<%
	}
%>
<%-- Try to login. --%>
<%
	boolean rememberMyPassword =
	loginForm.getRememberMyPassword() != null;
	try {
		LoginManager.login(request, response, loginName, password,
		rememberMyPassword);
	} catch (IncorrectPasswordException e) {
		errors.put("password", "Incorrect password");
	}
	if (errors.isEmpty()) {
		response.sendRedirect(response.encodeRedirectURL("MainPage.jsp"));
	} else {
		request.setAttribute("errors", errors);
%>
	<jsp:forward page="ShowLogin.jsp" />
<%}%>