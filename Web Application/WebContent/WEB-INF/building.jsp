<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
Add building<br>
${fn:escapeXml(message)}
<form action="building" method="POST">
	<label><b>Building Name*</b></label>
	<input type="text" placeholder="name" name="bName" value="${bName}" required><br>
	<label><b>Building Location*</b></label>
	<input type="text" placeholder="location" name="location" value="${location}" required><br>
	<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
	<button type="submit">Add</button>
</form>