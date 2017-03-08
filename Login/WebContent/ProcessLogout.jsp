<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
import="com.loginpage.LoginManager" %>
<%
LoginManager.logout(request, response);
response.sendRedirect(response.encodeRedirectURL("Index.jsp"));
%>%>