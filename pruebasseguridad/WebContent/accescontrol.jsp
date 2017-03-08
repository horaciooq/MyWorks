<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.sql.*"%>
    <%@ page import="javax.servlet.*" %>
	<%@ page import="javax.servlet.http.*" %>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h2>Datos del usuario</h2>
	  <%!
		Connection conConexion;
		PreparedStatement pst;
		ResultSet rs;
	  %>
			  <% 
			      String usuario= request.getParameter("user");
			  	  
			  %>
	<% while (rs.next()){ %>
		<p>Nombre: <%= rs.getString(2) %></p>
		<p>Apellido: <%= rs.getString(3) %></p>
		<p>Telefono: <%= rs.getString(4) %></p>
	<% } %>
	
</body>
</html>