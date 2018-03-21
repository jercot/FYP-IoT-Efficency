<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Insert title here</title>
		<link rel="stylesheet" type="text/css" href="css/dc.css"/>
		<link rel="stylesheet" type="text/css" href="css/layout.css"/>
	</head>
	<body>
		<form action="register" method="POST">		
		    <label><b>First Name*</b></label>
		    <input type="text" placeholder="name" name="fName" required> <br>
		    <label><b>Last Name*</b></label>
		    <input type="text" placeholder="name" name="lName" required> <br>
		    <label><b>Email*</b></label>
		    <input type="email" placeholder="email" name="email" required> <br>
		    <label><b>Password*</b></label>
		    <input type="password" placeholder="password" name="pass" required><br>
		    <label><b>Street</b></label>
		    <input type="text" placeholder="street" name="street"> <br>
		    <label><b>Town*</b></label>
		    <input type="text" placeholder="town" name="town" required> <br>
		    <label for="county"><b>County*</b></label>
		    <input type="text" placeholder="county" name="county" required> <br>
		    <label for="num"><b>Phone Number</b></label>
		    <input type="text" placeholder="number" name="num"> <br>
		    <button type="submit">Register</button>
		</form>
		<a href="<c:url value="/" />">Home</a>
		${requestScope.message == null ? '' : requestScope.message}
	</body>
</html>