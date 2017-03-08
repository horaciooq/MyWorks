<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<ul>
<li><a href="HelloUser?userName=Horacio+Ochoa">HelloUser by GET (userName = Horacio Ochoa)</a></li>
<li><a href="HelloUserByPost.html">HelloUser by POST</a></li>
<li><a href="MainPage">Portal-1 main page</a></li>
</ul>
<br>
<br>
<ul>
<li><a href="HelloUser.jsp?userName=Horacio+Ochoa">HelloUser by GET (userName = Horacio Ochoa)</a></li>
<li><a href="HelloUserByPost.html">HelloUser by POST</a></li>
<li><a href="<%= response.encodeURL("MainPage.jsp")%>">Portal-1 main page</a></li>
<li><a href="MainPage.jsp">Portal-2 main page</a></li>
</ul>
</body>
</html>