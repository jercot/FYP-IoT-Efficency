<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div id="dash">
	<h1>Account Details</h1>
	<table class="dashTable">
		<tr>
			<td>First Name:</td>
			<td>${firstName}</td>
		</tr>
		<tr>
			<td>Last Name:</td>
			<td>${lastName}</td>
		</tr>
		<tr>
			<td>Email:</td>
			<td>${email}</td>
		</tr>
		<tr>
			<td>Phone Number:</td>
			<td>${phone}</td>
		</tr>
		<tr>
			<td>Street:</td>
			<td>${street}</td>
		</tr>
		<tr>
			<td>Town:</td>
			<td>${town}</td>
		</tr>
		<tr>
			<td>County:</td>
			<td>${county}</td>
		</tr>
		<tr>
			<td>Registration Date:</td>
			<td>${regDate}</td>
		</tr>
		<tr>
			<td>Last Password Change:</td>
			<td>${lastPas}</td>
		</tr>
		<tr>
			<td>Number of houses in system:</td>
			<td>${houses}</td>
		</tr>
	</table>
	<a id="dashSet" href="settings">
		<div id="dashSet">
			Go to Account Settings
		</div>
	</a>
	<h1>Previous Activity</h1>
	<c:choose>
	<c:when test="${prev}">
	<table class="dashTable">
		<tr>
			<td>Last Login:</td>
			<td>${lastLog}</td>
		</tr>
		<tr>
			<td>Location:</td>
			<td>${location}</td>
		</tr>
		<tr>
			<td>System:</td>
			<td>${system}</td>
		</tr>
	</table>
	</c:when>
	<c:otherwise>
		<div id="previous">
			No Previous activity recorded for account
		</div>
	</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test="${not empty houseDash}">
	<h1>House List:</h1>
	<c:forEach items="${houseDash}" var="house">
	<div class="house">
		<div class="dashTitle">House: ${house.name}</div>
		<div class="dashLoc">
			Location: ${house.location}
			<a class="dashHou" href="house?bName=${house.name}">
				Go to:
			</a>
		</div>
		<c:forEach items="${house.rooms}" var="room" varStatus="loop">
		<div class="dashRoom roomColor${loop.index%2}">
			Room: ${room.name}<br> Floor: ${room.floor}
		</div>
		</c:forEach>
		<div class="clear"></div>
	</div>
	</c:forEach>
	</c:when>
	</c:choose>
</div>