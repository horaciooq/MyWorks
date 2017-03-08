<%@ page import="java.util.Map, java.util.HashMap,
com.loginpage.LoginForm" %>
<jsp:useBean
id="loginForm" scope="request"
class="com.loginpage.LoginForm"/>
<html>
<head>
<title>Portal-2 login form</title>
</head>
<body text="#000000" bgcolor="#ffffff">
	<%-- Get errors. --%>
	<%
		String loginNameErrorMessage = "";
		String passwordErrorMessage = "";
		Map errors = (Map) request.getAttribute("errors");
		if (errors != null) {
			String errorHeader = "<font color=\"red\"><b>";
			String errorFooter = "</b></font>";
		if (errors.containsKey("loginName")) {
			loginNameErrorMessage = errorHeader +
			errors.get("loginName") + errorFooter;
		}
		if (errors.containsKey("password")) {
			passwordErrorMessage = errorHeader +
			errors.get("password") + errorFooter;
		}
		}
	%>
	<form method="POST" action="<%= response.encodeURL("ProcessLogin.jsp")%>">
		<table width="100%" border="0" align="center" cellspacing="12">
			<%-- Login name --%>
			<tr>
				<th align="right" width="50%">
					Login name
				</th>
			<td align="left">
				<input type="text" name="loginName" value="<jsp:getProperty name="loginForm" property="loginName"/>" size="16" maxlength="16">
				<%= loginNameErrorMessage %>
			</td>
			</tr>
			<%-- Password --%>
			<tr>
				<th align="right" width="50%">
					Password
				</th>
			<td align="left">
				<input type="password" name="password" value="<jsp:getProperty name="loginForm" property="password"/>" size="16" maxlength="16">
				<%= passwordErrorMessage %>
			</td>
			</tr>
			<%-- Remember my password --%>
			<tr>
				<th align="right" width="50%">
					Remember my password (cookies must be enabled)
				</th>
			<td align="left">
				<input type="checkbox" name="rememberMyPassword" value="rememberMyPassword"
				<% if (loginForm.getRememberMyPassword() != null) { %>checked <% } %>>
			</td>
			</tr>
			<%-- Login button --%>
			<tr>
			<td width="50%"></td>
			<td align="left" width="50%">
				<input type="submit" value="Login">
			</td>
			</tr>
		</table>
	</form>
</body>
</html>