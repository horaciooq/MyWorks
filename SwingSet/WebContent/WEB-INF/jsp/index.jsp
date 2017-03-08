
<%
String title = "ESAPI SwingSet Interactive Application";
%>

<html>
<head>
<title><%=title%></title>
<link rel="stylesheet" type="text/css" href="style/style.css" />
</head>

<body>
<div id="container">
	<div id="holder">
		<div id="logo"><img src="style/images/owasp-logo_130x55.png" width="130" height="55" alt="owasp_logo" title="owasp_logo"></div>
<h1><%=title %></h1>
<div id="header"></div>
<hr> 

<!-- <h2><a href="main?function=InitialSetup">1. ESAPI SwingSet Initial Setup</a></h2> -->
<!-- <p>This tutorial will guide you on how to install, configure and run Swingset for the first time.</p> -->

<h2><a href="main?function=Introduction">1. ESAPI SwingSet Interactive - Introduction</a></h2>
<p>A few words about this application.</p>

<h2><a href="main?function=Login">2. Authentication</a></h2>
<p>Authentication is the process of determining whether someone or something is, in fact, who or what it is declared to be. The ESAPI Authenticator interface defines a set of methods for generating and handling account credentials and session identifiers. The goal of this interface is to encourage developers to protect credentials from disclosure to the maximum extent possible.</p>

<h2><a href="main?function=SessionManagement">3. Session Management</a></h2>
<p>Session management is the process of keeping track of a user's activity across sessions of interaction with the computer system. The ESAPI HTTPUtilities interface is a collection of methods that provide additional security related to HTTP requests, responses, sessions, cookies, headers, and logging.</p>

<h2><a href="main?function=AccessControl">4. Access Control</a></h2>
<p>Access Control is a process that defines each user's privileges on a system.
The ESAPI AccessController interface defines a set of methods that can be used in a wide variety of applications to enforce access control.</p>

<h2><a href="main?function=ValidateUserInput">5. Input Validation </a></h2>
<p>Input Validation is the process of ensuring that a program operates on clean, correct and useful data. The ESAPI Validator interface defines a set of methods for canonicalizing and validating untrusted input.</p>

<h2><a href="main?function=Encoding">6. Output Encoding/Escaping</a></h2>
<p>Encoding is the process of transforming information from one format into another. The ESAPI Encoder interface contains a number of methods for decoding input and encoding output so that it will be safe for a variety of interpreters.</p>

<h2><a href="main?function=Encryption">7. Cryptography</a></h2>
<p>Encryption is the process of transforming information (referred to as plaintext) using an algorithm (called cipher) to make it unreadable to anyone except those possessing special knowledge, usually referred to as a key. The ESAPI Encryptor interface provides a set of methods for performing common encryption, random number, and hashing operations.</p>

<h2><a href="main?function=Logging">8. Error Handling and Logging</a></h2>
<p>Error handling refers to the anticipation, detection, and resolution of programming, application, and communications errors. Data logging is the process of recording events, with an automated computer program, in a certain scope in order to provide an audit trail that can be used to understand the activity of the system and to diagnose problems. The ESAPI Logger interface defines a set of methods that can be used to log security events.</p>

<h2><a href="main?function=DataProtection">9. Data Protection</a></h2>
<p>Data Protection is the process of ensuring the prevention of misuse of computer data. The ESAPI HTTPUtilities interface is a collection of methods that provide additional security related to HTTP requests, responses, sessions, cookies, headers, and logging.</p>

<h2><a href="main?function=HttpSecurity">10. Http Security</a></h2>
<p>HTTP Security refers to the protection of HTTP requests, responses, sessions, cookies, headers and logging. The ESAPI HTTPUtilities interface is a collection of methods that provide additional security for all these.</p>


<%--
<h2>Input Validation, Encoding, and Injection</h2>
<ul>
<li><a href="main?function=OutputUserInput">Output User Input</a></li>
<li><a href="main?function=RichContent">Accept Rich Content</a></li>
<li><a href="main?function=ValidateUserInput">Validate User Input</a></li>
<li><a href="main?function=Encoding">Encode Output</a></li>
<li><a href="main?function=Canonicalize">Canonicalize Input</a></li>
</ul>

<h2>Cross Site Scripting</h2>
<ul>
<li><a href="main?function=XSS">Cross Site Scripting</a></li>
</ul>

<h2>Authentication and Session Management</h2>
<ul>
<li><a href="https://localhost:8443<%=request.getContextPath() %>/main?function=Login">Login</a></li>
<!-- <li><a href="main?function=Logout">Logout</a></li> (no implementation)-->
<li><a href="main?function=ChangePassword">Change Password</a></li>
<li><a href="main?function=ChangeSessionIdentifier">Change Session Identifier</a></li>
</ul>

<h2>Access Control and Referencing Objects</h2>
<ul>
<li><a href="main?function=ObjectReference">Reference a Server-Side Object</a></li>
<li><a href="main?function=AccessControl">Access Control</a></li>
</ul>

<h2>Encryption, Randomness, and Integrity</h2>
<ul>
<li><a href="main?function=Encryption">Encryption</a></li>
<li><a href="main?function=Randomizer">Randomizer</a></li>
<li><a href="main?function=Integrity">Integrity Seals</a></li>
<li><a href="main?function=GUID">Globally Unique IDs</a></li>
</ul>

<h2>Caching</h2>
<ul>
<li><a href="main?function=BrowserCaching">Browser Caching</a></li>
</ul>
--%>
<%@include file="footer.jsp" %>
