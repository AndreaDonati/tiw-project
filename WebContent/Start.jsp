<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*, java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Start</title>
</head>
<body>
	<h2>Using JavaBeans in JSP</h2>
	<jsp:useBean id="start" class="it.polimi.tiw.beans.StartBean"
		scope="session" />
	<jsp:setProperty name="start" property="msg"
		value="Ciao pezzo di stronzo." />
	<jsp:setProperty name="start" property="name" param="name" />
	<!-- Forcing the client to send a new request to avoid repeated POST on reload -->
	<%
	/*
		C'è uno standard da seguire per gli attributi? 
		Ad esempio, in questo caso, potrei mettere i parametri msg e name
		come attributi della sessione per poi recuperarli nella prossima 
		pagina.
	*/
	//session...
	response.sendRedirect("StartLanding.jsp");
	%>
</body>
</html>