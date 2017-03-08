<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.io.*, com.banorte.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<% 
		String datos_reporte="Este es un reporte: .....";
		String id = request.getParameter("id");
		verifiador ver = new verifiador();
		String reporte = ver.validar(id);
		//String reporte = id;
		File archivo = new File(reporte);
		FileWriter fichero = null;
	    PrintWriter pw = null;
	     try
	     {
	         fichero = new FileWriter(archivo,true);
	         pw = new PrintWriter(fichero);
			 pw.write(datos_reporte);
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	%>
</body>
</html>