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
			<div id="account">
				<div id="login">
					<form action="login" method="POST">
						<h3>Sign In</h3><br>
						To existing account
					    <input class="required" type="email" placeholder="Enter Email" name="email" required><br>
					    <input class="required" type="password" placeholder="Enter Password" name="pass" required><br>
						<a href="">Forgot Password?</a>
						<p id="message">${message}</p>
					    <button type="submit">Login</button>
					</form>
				</div>
				<div id="hl"></div>
				<div id="register">
					<h3>Register</h3><br>
					A new account<br><br>
					It's free to join, easy to set up. One account works with the website and mobile application.
					<a id="register" href="register">Register</a>
					<div id="mobile">
						<img src="images/google.png" id="google"/>
						<img src="images/apple.png" id="apple"/>
					</div>
				</div>
				<div id="vl"></div>
			</div>
			<div id="info">
				<div id="why">
					<h3>Why sign up?</h3>
					<ul>
						<li>Easy tracking of house statistics</li>
						<li>Quickly add houses to the system</li>
						<li>View visualisation of data collected</li>
					</ul>
				</div>
				<div id="hl"></div>
				<div id="what">
					<h3>Requirements</h3>
					<ul>
						<li>Arduino</li>
						<ul>
							<li>Ethernet Shield</li>
							<li>Photocell</li>
							<li>Temperature Sensor</li>
							<li>Humidity Sensor(DHT11)</li>
							<li>Ultrasonic Sensor(HC-SR04)</li>
						</ul>
						<li>Android Mobile</li>
						<li>Internet Connection</li>
						<li>Link to Schematics/Setup to be linked here</li>
					</ul>
				</div>
				<div id="vl"></div>
			</div>
		</div>
	</body>
</html>