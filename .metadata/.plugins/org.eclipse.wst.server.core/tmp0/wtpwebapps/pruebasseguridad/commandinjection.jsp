<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" 
    import = "org.owasp.esapi.*, java.io.*, java.util.*, com.banorte.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<% String archivo= request.getParameter("opcion"); %>
	<% String cmd = "cmd.exe /K del "+archivo; %>
	
	<% Runtime.getRuntime().exec(cadenaValidada); %>
	<%= "Archivo Borrado" %>
</body>
</html>