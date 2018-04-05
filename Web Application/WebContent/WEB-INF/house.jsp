<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:out value="${message}"/><hr>
GRAPH GOES UP HERE FOR EACH HOUSE<hr>
<div id="graph">
	<div id="lineChart"></div>
</div>

<c:choose>
	<c:when test="${not empty rooms}">
		Room List:<br>
		<c:forEach items="${rooms}" var="room">
			<c:out value="${room}"/> <br>
		</c:forEach>
		<hr>
	</c:when>
</c:choose>


Add Room  <c:out value="${name}"/>
<form action="room" method="POST">
	<label><b>Room Name*</b></label>
	<input type="text" placeholder="name" name="rName" required><br>
	<label><b>Floor*</b></label>
	<input type="number" min="1" max="25" placeholder="floor" name="floor" required>
	<input type="hidden" name="bName" value="${fn:escapeXml(bName)}">
	<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
	<button type="submit">Add</button>
</form>

<hr>

Modify Building:
<form action="house" method="POST">
	<label><b>Building Name</b></label>
	<input type="text" placeholder="name" name="bName"><br>
	<label><b>Building Location</b></label>
	<input type="text" placeholder="location" name="location"><br>
	<input type="hidden" name="pName" value="${fn:escapeXml(bName)}">
	<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
	<button type="submit">Modify</button>
</form>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="javascript/d3.js"></script>
<script src="javascript/crossfilter.js"></script>
<script src="javascript/dc.js"></script>
<script src="javascript/custom.js"></script>
<script>
	$(document).ready(function() {
		startVisual("${bName}");
	});
</script>