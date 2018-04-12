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
	<form action="Settings" method="POST">
	    <input type="hidden" name="type" value="pass">
	    <input type="password" placeholder="Previous Password" name="pPass" required> (required)<br>
	    <input type="password" placeholder="New Password" name="nPass1" required> (required)<br>
	    <input type="password" placeholder="New Password" name="nPass2" required> (required)<br>
	    <button type="submit">Register</button>
	</form>
	
	<h1>Security</h1>
	-Add 2FA
</div>