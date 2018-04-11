<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
loop over houses<br>
&nbsp;&nbsp;&nbsp;&nbsp;-house names<br>
&nbsp;&nbsp;&nbsp;&nbsp;-location<br>
&nbsp;&nbsp;&nbsp;&nbsp;-link to house<br>
&nbsp;&nbsp;&nbsp;&nbsp;loop over rooms<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;room names<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;room floor<br>

<table id="account">
	<tr>
		<th>Field:</th>
		<th>Value:</th>
	</tr>
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
<a href="settings">Go to Account Settings</a>
<c:choose>
<c:when test="${prev}">
<table id="login">
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
House List:<br>
<c:forEach items="${houseDash}" var="house">
<div class="house">
	${house.name}<br>
	${house.location}<br>
	<a href="house?bName=${house.name}">Go to:</a><br>
	<c:forEach items="${house.rooms}" var="room">
	-${room.name} = ${room.floor} <br>
	</c:forEach>
</div>
</c:forEach>
</c:when>
</c:choose>