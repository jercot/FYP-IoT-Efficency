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
		<div id="left">
			<div id="logo">
				IoT Efficiency
			</div>
			<div id="sideMenu">
				<a href="<c:url value="/" />">
					<div class="menu menuYellow">
						Dashboard <br>
					</div>
				</a>
				<hr class="left">
				<div class="houses">
					<div class="menu menuGreen">
						Houses <br>
					</div>
					<c:forEach items="${logged.buildings}" var="house">
						<a href="house?name=<c:out value="${house}"/>">
							<div class="menu menuHouse">
								${house}
							</div>
						</a>
					</c:forEach>
				</div>
				<a href="building">
					<div class="menu menuCyan">
						Add House <br>
					</div>
				</a>
				<hr class="left">
				<a href="compare">
					<div class="menu menuRed">
						Compare <br>
					</div>
				</a>
				<hr class="left">
				<a href="settings">
					<div class="menu menuOrange">
						Settings <br>
					</div>
				</a>
				<hr class="left">
				<a href="logout">
					<div class="menu menuWhite">
						Log Out
					</div>
				</a>
			</div>
		</div>
		<div id="right">
			<div id="title">
				Account Name - Selected House
			</div>
			<div id="main" class="main">
				<c:choose>
					<c:when test="${main == 'main'}">
						<%@ include file="main.jsp" %>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${main == 'building'}">
						<%@ include file="building.jsp" %>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${main == 'settings'}">
						<%@ include file="settings.jsp" %>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${main == 'compare'}">
						<%@ include file="compare.jsp" %>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${main == 'house'}">
						<%@ include file="house.jsp" %>
					</c:when>
				</c:choose>
			</div>
		</div>
	</body>
</html>