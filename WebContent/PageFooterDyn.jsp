<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Dynamically included page footer</title>
</head>
<body>
<hr>
<!-- Mi sa che devo dire che uso il bean user e poi prendere quello della sessione e poi prendere il suo nome -->
Sei uno stupidone <%= session.getAttribute("user") %>
<hr>
</body>
</html>