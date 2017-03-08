<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

<div id="navigation">
<a href="main">Home</a> | 
<b><a href="main?function=SessionManagement">Tutorial</a></b> | 
<a href="main?function=SessionFixation&lab">Lab : Session Fixation</a>| 
<a href="main?function=SessionFixation&solution">Solution</a> |
<a href="main?function=CSRF&lab">Lab : Cross Site Request Forgery</a> | 
<a href="main?function=CSRF&solution">Solution</a>
</div>
<div id="header"></div>
<p>
<hr>

<h2 align="center">Tutorial</h2>

<p>Whenever you authenticate, your application should change the session identifier it uses. This
helps to prevent someone from setting up a session, copying the session identifier, and then 
tricking a user into using the session. Because the attacker already knows the session identifier, 
they can use it to access the session after the user logs in, giving them full access. This attack
has been called "session fixation" among other things.</p>

<p>In principle, the attack is easy to defeat. All you have to do is change the session identifier when
a user logs in. Unfortunately, most platforms do not provide an easy way to actually accomplish this.
Fortunately, there is a simple method in ESAPI to do just this. This method is called automatically
if you are using ESAPI for authentication, but it can also be used as a standalone method.</p>

<p class="newsItem">
	<code>
	// add this call on every login<br />
	ESAPI.httpUtilities().changeSessionIdentifier();<br />
	</code>	
</p>

<p>You should also make sure that the session identifier is invalidated on the server and cleared
from the browser every time a user logs out.</p>

<%@include file="footer.jsp" %>
