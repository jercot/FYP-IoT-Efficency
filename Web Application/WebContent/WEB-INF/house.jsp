<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:choose>
	<c:when test="${not empty message}">
		<c:out value="${message}"/>
	</c:when>
</c:choose>
<div id="graph">
	<div id="chartSwitch">
		<svg xmlns="http://www.w3.org/2000/svg" viewbox="0 0 100 300" id="switchThermo">
			<circle cx="50" cy="245" r="33" stroke="none" />
			<rect x="30" y="130" width="40px" height="90px" />
			<path d="M34,40 34,223 a31,31 0 1,0 32,0 L66,40 a1,1 0 0,0 -32,0" 
			 fill="none" stroke="white" stroke-width="3px"/>
			<path d="M30,40 30,220 a35,35 0 1,0 40,0 L70,40 a1,1 0 0,0 -40,0" 
			 fill="none" stroke="black" stroke-width="6px"/>
			<path d="M70,200 55,200 M70,180 55,180 M70,160 55,160 
			M70,140 55,140 M70,120 55,120 M70,100 55,100 
			M70,80 55,80 M70,60 55,60" stroke="black" stroke-width="2px"/>
		</svg>
		<svg xmlns="http://www.w3.org/2000/svg" viewbox="0 0 200 300" id="switchBulb">
			<path d="M60,210 60,270 a10,1 0 0,0 80,0 L140,210" stroke="black" fill="grey" stroke-width="6px"/>
			<path d="M140,220 60,230 M140,240 60,250 M140,260 60,270" stroke="black" fill="grey" stroke-width="3px"/>
			<path d="M20,140 a90,90 0 1,1 160,0 L140,210 a8,1 0 1,1 -80,0 L20,140" stroke="black" stroke-width="6px" />
		</svg>
		<div id="topCharts">
			<div id="roomChart"></div>
			<div id="dayChart"></div>
		</div>
	</div>
	<div id="readingChart"></div>
	<div id="dragChart"></div>
	<div class="clear"></div>
</div>

<div id="roomList">
	<c:choose>
	<c:when test="${not empty rooms}">
	Room List:<br>
	<c:forEach items="${rooms}" var="room">
	<c:set var = "name" value = "${fn:replace(room.name, ' ', '_')}" />
	<div class="room" id="${name}">
		<span class="roomTitle">Room: ${fn:escapeXml(room.name)} - Floor: ${fn:escapeXml(room.floor)}</span> <br>
		<div class="thermo">
		<c:choose>
		<c:when test="${room.temp ne -1}">
			<div class="tIcon">
				<svg xmlns="http://www.w3.org/2000/svg" width="40" viewbox="0 0 100 300">
					<circle cx="50" cy="245" r="33" stroke="none" class="temp${room.heat}" />
					<c:forEach begin="0" end="${room.heat}" varStatus="loop">
					<rect x="30" y="${190-30*loop.index}" width="40px" height="30px" class="temp${room.heat}"/>
					</c:forEach>
					<path d="M34,40 34,223 a31,31 0 1,0 32,0 L66,40 a1,1 0 0,0 -32,0" 
					 fill="none" stroke="white" stroke-width="3px"/>
					<path d="M30,40 30,220 a35,35 0 1,0 40,0 L70,40 a1,1 0 0,0 -40,0" 
					 fill="none" stroke="black" stroke-width="6px"/>
					<path d="M70,200 55,200 M70,180 55,180 M70,160 55,160 
					M70,140 55,140 M70,120 55,120 M70,100 55,100 
					M70,80 55,80 M70,60 55,60" stroke="black" stroke-width="2px"/>
				</svg>
			</div>
			<div class="tText"> 
				${fn:escapeXml(room.temp)}&deg;C
			</div>
		</c:when>
		<c:otherwise>
			No Temperature Reading.
		</c:otherwise>
		</c:choose>
		</div>
		<div class="lumens">
			<c:choose>
			<c:when test="${room.light ne -1}">
				<div class="lIcon">
					<svg xmlns="http://www.w3.org/2000/svg" width="60" viewbox="0 0 200 300">
						<path d="M60,210 60,270 a10,1 0 0,0 80,0 L140,210" stroke="black" fill="grey" stroke-width="6px"/>
						<path d="M140,220 60,230 M140,240 60,250 M140,260 60,270" stroke="black" fill="grey" stroke-width="3px"/>
						<path d="M20,140 a90,90 0 1,1 160,0 L140,210 a8,1 0 1,1 -80,0 L20,140" stroke="black" stroke-width="6px" class="light${room.lum}"/>
					</svg>
				</div>
				<div class="lText"> 
					${fn:escapeXml(room.light)} lux
				</div>
			</c:when>
			<c:otherwise>
				No Light Level Reading.
			</c:otherwise>
			</c:choose>
		</div>
		<div class="edit">
			Modify Room:
			<form action="room" method="POST">
				<input type="text" placeholder="Room Name" name="rName"><br>
				<input type="number" min="1" max="25" placeholder="Floor" name="floor">
				<input type="hidden" name="type" value="modify">
				<input type="hidden" name="bName" value="${fn:escapeXml(bName)}">
				<input type="hidden" name="oName" value="${fn:escapeXml(room.name)}">
				<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
				<button type="submit">Modify</button>
			</form>
		</div>
	</div>
	</c:forEach>
	</c:when>
	<c:otherwise>
	No rooms in the system for the selected house.
	</c:otherwise>
	</c:choose>
</div>
<c:choose>
<c:when test="${logged.type == 'mobile'}">
	<div id="sensorList"></div>
</c:when>
</c:choose>
<br>
<hr>
<br>
<div id="addRoom">
	Add Room:  <c:out value="${name}"/>
	<form action="room" method="POST">
		<input type="text" placeholder="Room Name" name="rName" required><br>
		<input type="number" min="1" max="25" placeholder="Floor" name="floor" required>
		<input type="hidden" name="type" value="add">
		<input type="hidden" name="bName" value="${fn:escapeXml(bName)}">
		<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
		<button type="submit">Add</button>
	</form>
</div>

<br>
<hr>
<br>
<div id="modBuild">
	Modify Building:
	<form action="house" method="POST">
		<input type="text" placeholder="Building Name" name="bName"><br>
		<input type="text" placeholder="Location" name="location"><br>
		<input type="hidden" name="pName" value="${fn:escapeXml(bName)}">
		<input type="hidden" name="token" value="${fn:escapeXml(logged.token)}">
		<button type="submit">Modify</button>
	</form>
</div>

<script src="javascript/d3.js"></script>
<script src="javascript/crossfilter.js"></script>
<script src="javascript/dc.js"></script>
<script src="javascript/custom.js"></script>
<c:choose>
<c:when test="${logged.type == 'mobile'}">
<script>
	Android.scanLocal("${buckets}")
	console.log("${buckets}")
	//scanWithTokens("${rooms}")
</script>
</c:when>
</c:choose>
<script>
	$(document).ready(function() {
		startVisual("${bName}");
	});
</script>