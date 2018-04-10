<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>${website} - Registered</title>
		<link rel="stylesheet" type="text/css" href="css/register.css"/>
	</head>
	<body>
		<div id="title">
			<a href="<c:url value="/" />">
				<div id="home">
					Home
				</div>
			</a>
			<div id="icon">
				<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 270">
					<a id="hover" href="<c:url value="/" />">
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
					</a>
			    </svg>        
			</div>
		</div>
		<div id="content">
			<h1>Email Sent. Please check email address used to finish registration</h1>
			<a href="<c:url value="/" />">
				Return to homepage
			</a>
		</div>
	</body>
</html>