<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

<div id="navigation">
<a href="main">Home</a> | 
<b><a href="main?function=Login">Tutorial</a></b> | 
<a href="main?function=Login&lab">Lab : Authenticator Functions</a>| 
<a href="main?function=Login&solution">Solution</a> 
</div>
<div id="header"></div>
<p>
<hr>

<h2 align="center">Tutorial</h2>
<h4>Background</h4>
<p>Instance of the ESAPI's authenticator Class can be created as:</p>

<p class="newsItem"><code>Authenticator instance = ESAPI.authenticator();</code></p>

<p>If you plan on using the default Access Controller, you may need one or more of the following:</p>
<li> DataAccessRules.txt</li>
<li> FileAccessRules.txt</li>
<li> FunctionAccessRules.txt</li>
<li> ServiceAccessRules.txt</li>
<li> URLAccessRules.txt</li>

<p>You do not need users.txt.  ESAPI will create this file when your application requests to create its first user.</p>

<h4>Create Users</h4>
There are two ways to create users safely in ESAPI:
<p>Use main() from FileBasedAuthenticator to generate users.txt for the first time.  To do this:</p>
<p class="newsItem"><code>java -Dorg.owasp.esapi.resources="/path/resources" -classpath esapi.jar org.owasp.esapi.Authenticator username password role</pre></code></p>

To create users from within your application, use:
<p class="newsItem"><code>ESAPI.authenticator.createUser(username, password, password)</code></p>
Two copies of the new password are required to encourage user interface designers to include a "re-type password" field in their forms.

<br />''Note:Users created with the createUser method are disabled and locked by default.'' <br /><br /> You must call:
<p class="newsItem">
<code>
ESAPI.authenticator().getUser(username).enable();<br>
ESAPI.authenticator().getUser(username).unlock();
</code>
</p>

<h4>Login</h4>
If you use the default ESAPI authenticator, you will need your login page to use SSL, so be sure to have a keystore file and adjust your server configuration settings to account for this.  If you are using Apache Tomcat, please see the readme included in the latest release of the <a href="https://www.owasp.org/index.php/ESAPI_Swingset">ESAPI Swingset</a> for help setting up SSL.

<br/><br/>
<a href="http://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html#Configuration">Set up SSL for tomcat 6.0</a>

<br/><br/>

<p>To authenticate a user, call:</p>
<p class="newsItem"><code>User  user = ESAPI.authenticator().login(HTTPServletRequest, HTTPServletResponse);</code></p>

Be sure to set the UsernameParameterName and PasswordParameterName variables in ESAPI.properties.  The login method will use those variable names to take the username and password that the user entered from the HTTPRequest.

<h4>Logout</h4>
To log a User out, simply call:
<p class="newsItem">
<code>User  user = ESAPI.authenticator().logout;</code>
</p>

<h4>ESAPI User Interface: </h4>
<p>ESAPI's User Interface provides support to store lot of information that an application  must store for each user in order to enforce security properly.</p>
<p>A user account can be in one of several states. When first created, a User should be disabled, not expired, and unlocked. To start using the account, an administrator should enable the account. The account can be locked for a number of reasons, most commonly because they have failed login for too many times. Finally, the account can expire after the expiration date has been reached. The User must be enabled, not expired, and unlocked in order to pass authentication. </p>
<%@include file="footer.jsp" %>
