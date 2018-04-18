<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width">
		<title>${website} - Register</title>
		<link rel="stylesheet" type="text/css" href="css/register.css"/>
	</head>
	<body>
		<div id="title">
			<a href="<c:url value="/" />">
				<div id="home">
					Home
				</div>
			</a>
			<div id="icon">
				<%@ include file="logo.jsp" %>
			</div>
		</div> 
		<div id="content">
			<h1>Register New Account</h1>
			<div id="register">
				<form action="register" method="POST">
					<div id="input">
					    <input class="required" type="text" placeholder="First Name" name="fName" required> (required) <br>
					    <input class="required" type="text" placeholder="Last Name" name="lName" required> (required)<br>
					    <input class="required" type="email" placeholder="Email" name="email" required> (required)<br>
					    <input class="required" type="password" placeholder="Password" name="pass" required> (required)<br>
					    <input type="text" placeholder="Street" name="street"><br>
					    <input class="required" type="text" placeholder="Town" name="town" required> (required)<br>
					    <input class="required" type="text" placeholder="County" name="county" required> (required)<br>
					    <input type="text" placeholder="Phone Number" name="phone"><br><br>
				    </div>
				    <div id="terms">
				    	Terms &amp; Conditions:
				    	<div id="tText">
					    	Lorem ipsum dolor sit amet, dicunt nusquam oportere te pro, et causae theophrastus sit. Vix etiam perpetua voluptatum cu. Ex adipiscing reformidans interpretaris quo, ad sea dicta iuvaret veritus. Id eam nibh timeam reprimique, sed nihil suscipit singulis in, case tation facilis no eos. Ea tota putent vix. At posse impetus alienum pri, menandri mandamus id his, probo ipsum reprehendunt eu vel.
							<br><br>
							Est ea equidem commune. In eum voluptua placerat ponderum. Et elit iriure iisque nam, eos eu modus novum vulputate, possim vidisse adversarium duo ut. Homero democritum sea ei, ad has quando intellegebat. His ex omnes putant, harum sapientem no mel.
							<br><br>
							In lucilius detraxit voluptatum nam, diceret iudicabit ad sea, ea mel brute percipit omittantur. Tacimates rationibus ea sit, ei sed autem invidunt. Et usu summo philosophia, ut vel quando possim omittantur. Ut usu facer legere omittam, id minimum quaestio vel. Liber dolor at vel, qui duis mollis fierent et. Vim atomorum electram urbanitas an.
							<br><br>
							Ad est quem agam malorum. Vel at lorem audire maluisset. Te aliquip voluptaria duo, quot voluptatum persequeris quo id. Duo placerat adversarium no, eos ad munere luptatum deseruisse, vim impedit reprimique eu. Cum ea liber fabulas, doming repudiandae nam id. Mea prima reque qualisque et, ad per adipisci conceptam.
							<br><br>
							Te graeci euismod sit, decore accumsan definiebas vis in, cu consul urbanitas vis. Ad quas mucius vel, te accusam accusamus mel. Eam ut posse impedit. An porro saepe detraxit eam, cu vim diceret iracundia. Ridens nusquam inciderint per ex, vidit invidunt apeirian ius ut. Qualisque scripserit id vim, consul vivendum id quo. Labores gubergren nec ei, liber vituperata et nam.
				    	</div>
				    	<input type="checkbox" required/>I Agree to Terms &amp; Conditions<br>
				    </div>
					<div id="clear"></div>
					<button type="submit">Register</button><br>
				</form>
			</div> 
		</div>
	</body>
</html>