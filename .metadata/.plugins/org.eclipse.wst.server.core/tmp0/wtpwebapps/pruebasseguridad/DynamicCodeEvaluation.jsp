<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import ="javax.script.*, com.banorte.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<% ScriptEngineManager scriptEngineManager = new ScriptEngineManager(); %>
	<% ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("js"); %>
	<% String userOps = request.getParameter("operation"); %>
	<%  verificador ver =new verificador();%>
	<% String cadenaverificada=ver.verificar(userOps); %>
	<% Object result = scriptEngine.eval(cadenaverificada); %>
	<% //Object result = scriptEngine.eval(userOps); %>
	<%= result %>
	
	
	
	
</body>
</html>

