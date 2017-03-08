<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import = "org.owasp.esapi.ESAPI, com.banorte.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%@ page import = "org.owasp.esapi.ESAPI"%>
<% String nombre=request.getParameter("nom"); %>
<h1>El nombre es:</h1>
<% verifiador v=new verifiador(); %>
<h1><%=v.validar(nombre) %></h1>
</body>
</html>