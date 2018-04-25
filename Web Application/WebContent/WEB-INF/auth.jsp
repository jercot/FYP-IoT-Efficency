<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width">
		<title>IoT Efficiency</title>
		<link rel="stylesheet" type="text/css" href="css/homepage.css"/>
	</head>
	<body>
		<div id="title">
			<div id="icon">
				<%@ include file="logo.jsp" %>
			</div>
		</div> 
		<div id="content">
			<div id="outterAuth">
				<div id="auth">
					<form action="authenticate" method="POST">
						<h3>Enter Code</h3><br>
						Code available on mobile application:<br>
					    <input class="required" type="number" min="100000" max="999999" 
					    placeholder="Enter Code" name="code" autocomplete="off" required><br>
						<p id="message">${message}</p>
					    <button type="submit">Submit</button>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>