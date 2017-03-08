<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hello User</title>
</head>
<body text="#000000" bgcolor="#ffffff">
<h1>Hello <%= request.getParameter("userName") %> </h1>
<br><br><br>
Your name written ten times:<br>
<%
String name = request.getParameter("userName");
for (int i=0; i<10; i++) {
%>
<b><%= name %></b> <br>
<%
}
%>
</body>
</html>