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
				<%@ include file="logo.jsp" %>
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