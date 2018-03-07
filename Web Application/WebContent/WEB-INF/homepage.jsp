<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Insert title here</title>
		<link rel="stylesheet" type="text/css" href="css/dc.css"/>
		<link rel="stylesheet" type="text/css" href="css/layout.css"/>
	</head>
	<body>
		<form action="login" method="POST">		
		    <label for="uname"><b>Email</b></label>
		    <input type="email" placeholder="Enter Email" name="email" required>
		    <label for="psw"><b>Password</b></label>
		    <input type="password" placeholder="Enter Password" name="pass" required>
		    <button type="submit">Login</button>
		</form>
		<a href="register">Register</a>
	</body>
</html>