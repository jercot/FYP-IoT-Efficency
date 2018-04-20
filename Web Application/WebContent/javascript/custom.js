var roomChart = dc.pieChart("#roomChart");
var dayChart = dc.rowChart("#dayChart");
var readingChart = dc.lineChart("#readingChart");
var dragChart = dc.barChart("#dragChart");
var switchThermo = $("#switchThermo");
var switchBulb = $("#switchBulb");
var sensor = 0;
var color = ["#8abee2", "#3cbe98", "#f99e18", "#fe2918", "#fcd812", "#c7d892"];
var filter;
var max = 0, min = 9223372036854775807;
var resizeId;

function startVisual(house) {
	$.post("data", {bName: house},function(data) {
		$("#graph").css({display: "block"});
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
		setStats(data.records);
		break;
	}
}

function setStats(data) {
	data.forEach(function (d) {
		var date = new Date(+d.ti);
		var formatTime = d3.time.format("%A");
		d.day = formatTime(date);
		d.min = d3.time.minute(date);
	});
	graphScale = [new Date(+data[0].ti), new Date(+data[data.length-1].ti)];
	filter = crossfilter(data),
	roomDimension = filter.dimension(function (d) {
		return d.na;
	}),
	roomGroup = roomDimension.group().reduceSum(function(d) {
		return 1;
	}),
	readingDimension = filter.dimension(function (d) {
		return new Date(+d.ti);
	}),
	readingGroup = readingDimension.group().reduce(reduceAdd, reduceRemove, reduceInitial),
	dayDimension = filter.dimension(function(d) {
		return d.day;
	}),
	dayGroup = dayDimension.group().reduceSum(function(d) {
		return 1;
	}),
	dragDimension = filter.dimension(function(d) {
		return d.ti;
	}),
	dragGroup = readingDimension.group().reduceSum(function(d) {
		return 1;
	});
	readingGroup = removeEmptyReadingBins(readingGroup);
	dragGroup = removeEmptyDragBins(dragGroup);
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
	.margins({top: 0, right: 30, bottom: 55, left: 30})
	.dimension(readingDimension)
	.group(readingGroup)
	//.rangeChart(dragChart)
	.x(d3.scale.linear())
	.elasticX(true)
	.round(d3.time.month.round)
	.xUnits(d3.time.months)
	.elasticY(true)
	.brushOn(false)
	.renderHorizontalGridLines(true)
	.valueAccessor(function(p) {
		if(sensor===0)
			return p.value.count > 0 ? p.value.temp / p.value.count : 0; 
			return p.value.count > 0 ? p.value.light / p.value.count : 0; 
	})
	.title(function(p) {
		var formatTime = d3.time.format("%a %d %b - %H:%M");
		if(sensor===0)
			return (p.value.count > 0 ? "Temp level: " + numberFormat(p.value.temp / p.value.count) + "\u00B0C" : "No Reading")
			 + "\nDate: " + formatTime(new Date(+p.key));
		return (p.value.count > 0 ? "Light level: " + p.value.light / p.value.count +" lux": "No Reading")
		 + "\nDate: " + formatTime(new Date(+p.key));
	});
	readingChart
	.xAxis()
	.orient("bottom").ticks(10)
	.tickFormat(function(d,v,p,a,c){
		var formatTime = d3.time.format("%b %d - %H:%M");
		return formatTime(new Date(+d));
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
	})
	.on("filtered", function() {
		dc.redrawAll();
	});

	dayChart
	.dimension(dayDimension)
	.group(dayGroup)
	.controlsUseVisibility(true)
	.margins({top: 0, right: 5, bottom: -1, left: 0})
	.colors(d3.scale.category10())
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
	.dimension(dragDimension)
	.group(dragGroup)
	.margins({top: 0, right: 30, bottom: 20, left: 30})
	.valueAccessor(function(p) {
		if(p.value>0)
			return 1;
		return 0;
	})
	.gap(7)
	.x(d3.scale.linear().domain(graphScale))
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

function removeEmptyDragBins(source) {
    return {
        all: function () {
            return source.all().filter(function (d) {
                return d.value>0;
            });
        }
    };
}

function removeEmptyReadingBins(source) {
    return {
        all: function () {
            return source.all().filter(function (d) {
                return d.value.count>0;
            });
        }
    };
}

$(document).ready(function() {
	$(window).resize(function() {
	    clearTimeout(resizeId);
	    resizeId = setTimeout(doneResizing, 500);
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

function doneResizing(){
	readingChart
	.width($("#graph").width())
	.rescale();
	dragChart
	.width($("#graph").width())
	.rescale();

	dc.redrawAll();
}