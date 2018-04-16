<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width">
		<title>IoT Efficiency - ${subtitle}</title>
		<link rel="stylesheet" type="text/css" href="css/dc.css"/>
		<link rel="stylesheet" type="text/css" href="css/layout.css"/>
	</head>
	<body>
		<c:choose>
		<c:when test="${logged.type != 'mobile'}">
		<div id="sideBar">
			<div id="logo">
				IoT Efficiency
			</div>
			<div id="sideMenu">
				<a href="<c:url value="/" />">
					<div class="menu" id="menuYellow">
						Dashboard <br>
					</div>
				</a>
				<hr class="left">
				<div id="houses">
					<div class="menu" id="menuGreen">
						Houses <br>
					</div>
					<div id="houseList">
						<c:forEach items="${logged.buildings}" var="house">
						<a href="house?bName=<c:out value="${house}"/>">
							<div class="menu menuHouse houseMargin">
								${fn:escapeXml(house)}
							</div>
						</a>
						</c:forEach>
					</div>
				</div>
				<a href="building">
					<div class="menu" id="menuCyan">
						Add House <br>
					</div>
				</a>
				<!--<hr class="left">
				<a href="compare">
					<div class="menu" id="menuRed">
						Compare <br>
					</div>
				</a>-->
				<hr class="left">
				<div id="burger">
					<div id="filling">
						<a href="settings">
							<div id="menuOrange" class="menu fillingMargin">
								Settings <br>
							</div>
						</a>
						<hr class="left">
						<a href="logout">
							<div id="menuWhite" class="menu fillingMargin">
								Log Out
							</div>
						</a>
					</div>
					<div class="slice"></div>
					<div class="slice"></div>
					<div class="slice"></div>
				</div>
			</div>
		</div>
		</c:when>
		</c:choose>
		<div id="content">
			<c:choose>
			<c:when test="${logged.type != 'mobile'}">
			<div id="title">
				${fn:escapeXml(logged.email)} - ${fn:escapeXml(subtitle)}
			</div>
			</c:when>
			</c:choose>
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