<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Insert title here</title>
		<link rel="stylesheet" type="text/css" href="css/dc.css"/>
		<link rel="stylesheet" type="text/css" href="css/layout.css"/>
	</head>
	<body>
			<div id="logo">
				IoT Efficiency
			</div>
			<div id="sideMenu">
				<a href="">
					<div class="menu menuYellow">
						Dashboard <br>
					</div>
				</a>
				<hr class="left">
				<div class="houses">
					<div class="menu menuGreen">
						Houses <br>
					</div>
					<div class="menu menuHouse">
						House 1
					</div>
				</div>
				<div class="menu menuCyan">
					Add House <br>
				</div>
				<hr class="left">
				<a href="compare">
					<div class="menu menuRed">
						Compare <br>
					</div>
				</a>
				<hr class="left">
				<a href="settings">
					<div class="menu menuOrange">
						Settings <br>
					</div>
				</a>
				<hr class="left">
				<a href="logout">
					<div class="menu menuWhite">
						Log Out
					</div>
				</a>
			</div>
			<div id="title">
				Account Name - Selected House
			</div>
			<div id="main" class="main">
				
				dc.js example taken from github <a href="http://dc-js.github.io/dc.js/">here</a> <br>
				sensor data <a href="test.txt">here</a> <br>
				database data <a href="database">here</a>
				
				<br><br> Github?
				<br> Sensor - Average/simple moving average
				<br> Use datasource for tomcat
				<br><br>
				
				<div class="row">
    <div id="yearly-bubble-chart" class="dc-chart">
        <strong>Yearly Performance</strong> (radius: fluctuation/index ratio, color: gain/loss)
        <a class="reset" href="javascript:yearlyBubbleChart.filterAll();dc.redrawAll();"
           style="display: none;">reset</a>

        <div class="clearfix"></div>
    </div>
</div>

<div class="row">
    <div id="gain-loss-chart">
        <strong>Days by Gain/Loss</strong>
        <a class="reset" href="javascript:gainOrLossChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>

        <div class="clearfix"></div>
    </div>

    <div id="quarter-chart">
        <strong>Quarters</strong>
        <a class="reset" href="javascript:quarterChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>

        <div class="clearfix"></div>
    </div>

    <div id="day-of-week-chart">
        <strong>Day of Week</strong>
        <a class="reset" href="javascript:dayOfWeekChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>

        <div class="clearfix"></div>
    </div>

    <div id="fluctuation-chart">
        <strong>Days by Fluctuation(%)</strong>
        <span class="reset" style="display: none;">range: <span class="filter"></span></span>
        <a class="reset" href="javascript:fluctuationChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>

        <div class="clearfix"></div>
    </div>
	</div>
	
	<div class="row">
	    <div id="monthly-move-chart">
	        <strong>Monthly Index Abs Move &amp; Volume/500,000 Chart</strong>
	        <span class="reset" style="display: none;">range: <span class="filter"></span></span>
	        <a class="reset" href="javascript:moveChart.filterAll();volumeChart.filterAll();dc.redrawAll();"
	           style="display: none;">reset</a>
	
	        <div class="clearfix"></div>
	    </div>
	</div>
	
	<div class="row">
	    <div id="monthly-volume-chart">
	    </div>
	    <p class="muted pull-right" style="margin-right: 15px;">select a time range to zoom in</p>
	</div>
	
	<div class="row">
	    <div>
	        <div class="dc-data-count">
	            <span class="filter-count"></span> selected out of <span class="total-count"></span> records | <a
	                href="javascript:dc.filterAll(); dc.renderAll();">Reset All</a>
	        </div>
	    </div>
	    <table class="table table-hover dc-data-table">
	    </table>
	</div>


			</div>
			<script type="text/javascript" src="javascript/colorbrewer.js"></script>
			<script type="text/javascript" src="javascript/d3.js"></script>
			<script type="text/javascript" src="javascript/crossfilter.js"></script>
			<script type="text/javascript" src="javascript/dc.js"></script>
			<script type="text/javascript" src="javascript/jquery-3.2.1.min.js"></script>
			<script type="text/javascript" src="javascript/custom.js"></script>
	</body>
</html>