<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>IoT Efficiency</title>
		<link rel="stylesheet" type="text/css" href="css/homepage.css"/>
	</head>
	<body>
		<div id="title">
			<div id="icon">
				<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 270">
					<path d="M50,200 a40,40 0,1 1 0,-80 a10,10 0,1 1 50,-50 a60,60 0,1 1 130,40 a10,10 0,1 1 10,90 L50,200" fill="black" stroke="white" stroke-width="5px"/>
					<path d="M95,120 M90,125 145,80 200,125 M195,120 195,200 95,200 95, 120" stroke="white" stroke-width="3px" fill="white"/>
					<text x="5" y="250" font-weight="bold" font-size="45px" fill="white">IoT Efficiency</text>
				    <svg x="110" y="20" width="30" viewbox="0 0 200 300">
						<path d="M60,210 60,270 a10,1 0 0,0 80,0 L140,210" stroke="black" fill="grey" stroke-width="6px"/>
						<path d="M140,220 60,230 M140,240 60,250 M140,260 60,270" stroke="black" fill="grey" stroke-width="3px"/>
						<path d="M20,140 a90,90 0 1,1 160,0 L140,210 a8,1 0 1,1 -80,0 L20,140" stroke="black" stroke-width="6px" fill="yellow"/>
					</svg>
				    <svg x="150" y="10" width="25" viewbox="0 0 100 300">
						<circle cx="50" cy="245" r="33" stroke="none" fill="blue" />
						<rect x="30" y="190" width="40px" height="30px" fill="blue"/>
						<path d="M34,40 34,223 a31,31 0 1,0 32,0 L66,40 a1,1 0 0,0 -32,0" 
						 fill="none" stroke="white" stroke-width="3px"/>
						<path d="M30,40 30,220 a35,35 0 1,0 40,0 L70,40 a1,1 0 0,0 -40,0" 
						 fill="none" stroke="black" stroke-width="6px"/>
						<path d="M70,200 55,200 M70,180 55,180 M70,160 55,160 
						M70,140 55,140 M70,120 55,120 M70,100 55,100 
						M70,80 55,80 M70,60 55,60" stroke="black" stroke-width="2px"/>
					</svg>
			    </svg>        
			</div>
		</div> 
		<div id="content">
			<div id="account">
				<div id="login">
					<form action="login" method="POST">
						<h3>Sign In</h3><br>
						To existing account
					    <input type="email" placeholder="Enter Email" name="email" required><br>
					    <input type="password" placeholder="Enter Password" name="pass" required><br>
						<a href="">Forgot Password?</a>
					    <button type="submit">Login</button>
					    ${message}
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