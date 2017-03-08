<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	  <title>SQL-Injection</title>
	</head>
	<body>
			<%@ page import="java.sql.*" %>
			<%!
			  Connection conConexion;
			  PreparedStatement scSQL;
			  ResultSet rsListaRegistros;
			  ResultSetMetaData lsDatos;
			%>
			<%
				String nombre = request.getParameter("nom");
			%>
			<%
			String query="SELECT * FROM personas WHERE nombre=?";
			  Class.forName("com.mysql.jdbc.Driver").newInstance();
			  conConexion = DriverManager.getConnection("jdbc:mysql://localhost/seguridad",
			      "user","password");
			  scSQL = conConexion.prepareStatement(query);
			  scSQL.setString(1, nombre);			  
			  rsListaRegistros = scSQL.executeQuery();
			  lsDatos = rsListaRegistros.getMetaData();
			%>
			
			<table width="100%" border="1">
				<tr>
					<% 
					  for(int iCont = 1;
					  iCont <= lsDatos.getColumnCount(); 
					  iCont++ ) { 
					%>
					<th>
						<%-- Nombres de las columnas de la tabla MySQL --%>
						<%= lsDatos.getColumnLabel(iCont) %>
					</th>
					<% } %>
				</tr>
				<% while(rsListaRegistros.next()) { %>
				<tr>
					<% 
					  for(int iCont=1; 
					  iCont <= lsDatos.getColumnCount(); 
					  iCont++ ) { 
					%>
					<%-- Obtenemos el valor de los registros --%>
					<% 
					  if (iCont == 4) { 
					%>
					<td>
						<%= rsListaRegistros.getInt(iCont) %>
					</td>
					<% 
					  } else { 
					%>
					<td>
						<%= rsListaRegistros.getString(iCont) %>
					</td>
					<% } } %>
				</tr>
				<% } %>
			</table>
			<br><br><br>
			<%= query %>
		</body>
</html>