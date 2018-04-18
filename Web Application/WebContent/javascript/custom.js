var roomChart = dc.pieChart("#roomChart");
var dayChart = dc.rowChart("#dayChart");
var readingChart = dc.lineChart("#readingChart");
var dragChart = dc.barChart("#dragChart");
var switchThermo = $("#switchThermo");
var switchBulb = $("#switchBulb");
var sensor = 0;
var saveData = null;
var color = ["#8abee2", "#3cbe98", "#f99e18", "#fe2918", "#fcd812", "#c7d892"];
var filter;

function startVisual(house) {
	$.post("data", {bName: house},function(data) {
		console.log("Post Request Success");
		switchStatement(data);
	},"json")
	.fail(function(d) {
		console.log("Post Request Failed");
	});
}

function switchStatement(data) {
	switch(data.code) {
	case 1:
		saveData = data.records;
		setStats(data.records);
		break;
	}
}

function setStats(data) {
	data.forEach(function (d) {
		var date = new Date(+d.ti);
		var formatTime = d3.time.format("%A");
		d.day = formatTime(date);
		d.month = d3.time.month(date);
		d.hour = d3.time.hour(date);
		d.min = d3.time.minute.range(d.hour, date, 15);
		d.min = d.min[d.min.length-1]
	});
	graphScale = [new Date(+data[0].ti-9000000), new Date(+data[data.length-1].ti+9000000)];
	filter = crossfilter(data),
	roomDimension = filter.dimension(function (d) {
		return d.na;
	}),
	roomGroup = roomDimension.group().reduceSum(function(d) {
		return 1;
	}),
	readingDimension = filter.dimension(function (d) {
		return d.min;
	}),
	readingGroup = readingDimension.group().reduce(reduceAdd, reduceRemove, reduceInitial),
	dayDimension = filter.dimension(function(d) {
		return d.day;
	}),
	dayGroup = dayDimension.group().reduceSum(function(d) {
		return 1;
	}),
	dragGroup = readingDimension.group().reduceSum(function(d) {
		return 1;
	});
	function reduceAdd(p, v) {
		++p.count;
		p.temp += v.te;
		p.light += v.li;
		return p;
	}
	function reduceRemove(p, v) {
		--p.count;
		p.temp -= v.te;
		p.light -= v.li;
		return p;
	}
	function reduceInitial(p, v) {
		return {count: 0, temp: 0, light: 0};
	}

	createGraph();
}

function createGraph() {
	var numberFormat = d3.format('.2f');
	readingChart
	.renderArea(true)
	.width($("#graph").width())
	.height(200)
	.transitionDuration(1000)
	.rangeChart(dragChart)
	.margins({top: 0, right: 50, bottom: 25, left: 40})
	.dimension(readingDimension)
	.x(d3.time.scale().domain(graphScale))
	.round(d3.time.month.round)
	.xUnits(d3.time.months)
	.elasticY(true)
	.brushOn(false)
	.renderHorizontalGridLines(true)
	.group(readingGroup)
	.valueAccessor(function(p) {
		if(sensor===0)
			return p.value.count > 0 ? p.value.temp / p.value.count : 0; 
			return p.value.count > 0 ? p.value.light / p.value.count : 0; 
	})
	.title(function(p) {
		var formatTime = d3.time.format("%a %d %b - %H:%M");
		if(sensor===0)
			return "Temp level: " + numberFormat(p.value.count > 0 ? p.value.temp / p.value.count : 0) + " \u00B0C\nDate: " + formatTime(p.key);
		return "Light level: " + (p.value.count > 0 ? p.value.light / p.value.count : 0) + " lux\nDate: " + formatTime(p.key);
	});

	roomChart
	.dimension(roomDimension)
	.group(roomGroup)
	.colors(d3.scale.ordinal().range(color))
	.title(function(d) {
		return "Room: " + d.key + "\nReadings: " + d.value;
	})
	.valueAccessor(function(p) {
		return 1;
	});

	dayChart
	.dimension(dayDimension)
	.group(dayGroup)
	.elasticX(true)
	.controlsUseVisibility(true)
	.margins({top: 0, right: 5, bottom: -1, left: 0})
	.valueAccessor(function(p) {
		return 1;
	})
	.title(function(p) {
		return "Readings: " + p.value;
	})
	.ordering(function(d) {
		if(d.key === "Monday") return 0;
		else if(d.key === "Tuesday") return 1;
		else if(d.key === "Wednesday") return 2;
		else if(d.key === "Thursday") return 3;
		else if(d.key === "Friday") return 4;
		else if(d.key === "Saturday") return 5;
		else if(d.key === "Sunday") return 6;
	});
	
	dragChart
	.height(60)
    .width($("#graph").width())
    .margins({top: 0, right: 50, bottom: 20, left: 50})
    .dimension(readingDimension)
    .group(dragGroup)
    .valueAccessor(function(p) {
		return 1;
	})
    .centerBar(true)
    .gap(7)
    .x(d3.time.scale().domain(graphScale))
    .round(d3.time.minute.round)
    .alwaysUseRounding(true)
    .xUnits(d3.time.hours)
    .xAxisLabel("Drag for Timeline")
    .yAxis().ticks(0);
    dragChart.xAxis().tickValues([]);

	switchThermo.css({fill: "orange"});
	switchBulb.css({fill: "none"});

	dc.renderAll();
}

function save_first_order() {
	var original_value = {}; // 1
	return function(chart) {
		chart.group().all().forEach(function(kv) { // 2
			original_value[kv.key] = kv.value;
		});
		chart.ordering(function(kv) { // 3
			return -original_value[kv.key];
		});
	};
}

$(document).ready(function() {
	$(window).on("resize", function() {
		readingChart
		.width($("#graph").width())
		.rescale();
		dragChart
		.width($("#graph").width())
		.rescale();

		dc.redrawAll();
	});

	switchThermo.on("click", function() {
		if(sensor!=0) {
			sensor = 0;
			switchThermo.css({fill: "orange"});
			switchBulb.css({fill: "none"});
			dc.redrawAll();
		}
	});

	switchBulb.on("click", function() {
		if(sensor!=1) {
			sensor = 1;
			switchBulb.css({fill: "orange"});
			switchThermo.css({fill: "none"});
			dc.redrawAll();
		}
	});
});