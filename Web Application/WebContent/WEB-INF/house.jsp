<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
GRAPH GOES UP HERE FOR EACH HOUSE<hr>

<c:choose>
	<c:when test="${rooms!=null}">
		Room List:<br>
		<c:forEach items="${rooms}" var="room">
			${room} <br>
		</c:forEach>
	</c:when>
</c:choose>

<hr>

${message}
Add Room  ${name}
<form action="room" method="POST">
	<label><b>Room Name*</b></label>
	<input type="text" placeholder="name" name="rName" required><br>
	<label><b>Floor*</b></label>
	<input type="number" min="1" max="25" placeholder="floor" name="floor" required>
	<input type="hidden" name="bName" value="${name}">
	<input type="hidden" name="token" value="${logged.token}">
	<button type="submit">Add</button>
</form>