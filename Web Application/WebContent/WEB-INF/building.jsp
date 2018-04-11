<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
${fn:escapeXml(message)}
<div id="addBuilding">
	<h1>Add building</h1>
	<form action="building" method="POST">
		<input type="text" placeholder="Building Name" name="bName" value="${bName}" required><br>
		<input type="text" placeholder="Location" name="location" value="${location}" required><br>
		<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
		<button type="submit">Add</button>
	</form>
</div>