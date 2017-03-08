<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

<div id="navigation">
<a href="main">Home</a> | 
<a href="main?function=Logging">Logging Tutorial</a> | 
<a href="main?function=Logging&lab">Lab: Logging</a> | 
<a href="main?function=Logging&solution">Solution</a> | 
<b><a href="main?function=ErrorHandling">Error Handling Tutorial</a></b>
<a href="main?function=ErrorHandling&lab">Lab: Error Handling</a> | 
<a href="main?function=ErrorHandling&solution">Lab: Error Handling</a>
</div>
<div id="header"></div>
<p>
<hr>

<h2 align="center">Tutorial</h2>

<p>EnterpriseSecurityException is the base class for all security related exceptions. You should pass in the root cause exception where possible. Constructors for classes extending EnterpriseSecurityException should be sure to call the appropriate super() method in order to ensure that logging and intrusion detection occur properly.</p> 

<p>All EnterpriseSecurityExceptions have two messages, one for the user and one for the log file. This way, a message can be shown to the user that doesn't contain sensitive information or unnecessary implementation details. Meanwhile, all the critical information can be included in the exception so that it gets logged.</p> 

<p>Note that the "logMessage" for ALL EnterpriseSecurityExceptions is logged in the log file. This feature should be used extensively throughout ESAPI implementations and the result is a fairly complete set of security log records. ALL EnterpriseSecurityExceptions are also sent to the IntrusionDetector for use in detecting anomolous patterns of application usage.</p> 


<%@include file="footer.jsp" %>
