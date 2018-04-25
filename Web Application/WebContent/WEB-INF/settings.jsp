<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div id="settings">
	${settings}
	<h1>Profile:</h1>
	<form action="settings" method="POST">	
	    <input type="hidden" name="type" value="profile">	
	    <input type="text" placeholder="First Name" name="fName"><br>
	    <input type="text" placeholder="Last Name" name="lName"><br>
	    <input type="email" placeholder="Email" name="email"><br>
	    <input type="text" placeholder="Phone Number" name="phone"> <br>
		<button type="submit">Update Profile</button>
	</form>
	
	<h1>Address:</h1>
	<form action="settings" method="POST">
	    <input type="hidden" name="type" value="address">
	    <input type="text" placeholder="Street" name="street"> <br>
	    <input type="text" placeholder="Town" name="town"><br>
	    <input type="text" placeholder="County" name="county"><br>
	    <button type="submit">Update Address</button>
	</form>
	
	<h1>Password:</h1>
	<form action="settings" method="POST">
	    <input type="hidden" name="type" value="pass">
	    <input class="required" type="password" placeholder="Current Password" name="cPass" required> (required)<br>
	    <input class="required" type="password" placeholder="New Password" name="nPass1" required> (required)<br>
	    <input class="required" type="password" placeholder="New Password" name="nPass2" required> (required)<br>
	    <button type="submit">Update Password</button>
	</form>
	<c:choose>
		<c:when test="${logged.type == 'mobile'}">
	<h1>Security</h1>
	<form id="secSet" action="settings" method="POST">
	    <input type="hidden" name="type" value="2fa">
	    Enable 2 factor authentication: <input class="2fa" type="checkbox" placeholder="Current Password" name="2fa"><br>
	    <button type="submit">Update Security</button>
	</form>
	<script>var t = "${device!=null? device : 'null'}"</script>
	</c:when>
	</c:choose>
</div>