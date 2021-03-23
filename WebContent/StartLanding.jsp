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
	<h2>Redirected to avoid POST on refresh</h2>
	<jsp:useBean id="start" class="it.polimi.tiw.beans.StartBean"
		scope="session" />
	<jsp:getProperty name="start" property="msg" />
	<br />
	<!-- di seguito uno scripplet che in teoria mostra il nome scelto tramite post -->
	Hai scelto il nome: "<jsp:getProperty name="start" property="name" />",
	che nome dimmerda.
</body>
</html>