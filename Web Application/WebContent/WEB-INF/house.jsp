Add Room  ${name}
<form action="room" method="POST">
	<label><b>Room Name*</b></label>
	<input type="hidden" name="name" value="${name}">
	<input type="text" placeholder="name" name="rName" required>
	<button type="submit">Add</button>
</form>