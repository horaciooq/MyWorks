<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.loginpage.LoginManager" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body text="#000000" bgcolor="#ffffff" link="#000ee0" vlink="#551a8b"
	alink="#000ee0">
	<%
		String loginName = LoginManager.getLoginName(request);
		if (loginName == null) { // User has not logged in yet.
		response.sendRedirect(response.encodeRedirectURL(
		"ShowLogin.jsp"));
		} else {
	%>
	<%-- User has logged in. --%>
	<h1>Hello <%=loginName%> !!</h1>
	<br>
	<br>
	<br>
	<a href="<%= response.encodeURL("Index.jsp") %>">
	Servlet and JSP tutorial main page</a>
	<br>
	<a href="<%= response.encodeURL("ProcessLogout.jsp") %>">Logout</a>
	<% } %>
</body>
</html>