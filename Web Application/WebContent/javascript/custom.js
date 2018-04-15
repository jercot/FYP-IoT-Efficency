var roomChart = dc.pieChart("#roomChart");
var readingChart = dc.lineChart("#readingChart");
var sensor = 0;
var saveData = null;
var color = ["#8abee2", "#3cbe98", "#f99e18", "#fe2918", "#fcd812", "#c7d892"];

function startVisual(house) {
	$.post("data", {bName: house},function(data) {
		console.log("Post Request Success");
		switchStatement(data);
	},"json")
	.fail(function(d) {
		console.log("Post Request Failed");
	});
	/*$.ajax({
	    type : 'POST',
	    url : "data",
	    data : {bName: house},
	    dataType : "json",
	    success : function(data) {
	    	createGraph(data);
	    }
	});*/
}

function switchStatement(data) {
	switch(data.code) {
	case 1:
		saveData = data.records;
		createGraph(data.records);
		break;
	}
}

function createGraph(data) {
    var numberFormat = d3.format('.2f');
	data.forEach(function (d) {
		var date = new Date(+d.ti);
		var formatTime = d3.time.format("%d/%m/%Y");
		d.date = formatTime(date);
		d.month = d3.time.month(date);
		d.hour = d3.time.hour(date);
		d.min = d3.time.minute.range(d.hour, date, 15);
		d.min = d.min[d.min.length-1]
	});
	scale = [getDate(data[0].date, 1), getDate(data[data.length-1].date, 0)];
	var filter = crossfilter(data),
	roomDimension = filter.dimension(function (d) {
		return d.na;
	}),
	roomGroup = roomDimension.group().reduceSum(function(d) {
		return +1;
	}),
	readingDimension = filter.dimension(function (d) {
		return d.min;
	}),
	readingGroup = readingDimension.group().reduce(reduceAdd, reduceRemove, reduceInitial);
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
	
	roomChart
	.dimension(roomDimension)
	.group(roomGroup)
	.colors(d3.scale.ordinal().range(color))
	.title(function(d) {
		return "Room: " + d.key + "\nReadings: " + d.value;
	});

	readingChart
	.renderArea(true)
	.width(990)
	.height(200)
	.transitionDuration(1000)
	.margins({top: 0, right: 50, bottom: 25, left: 40})
	.dimension(readingDimension)
	.x(d3.time.scale().domain(scale))
	.round(d3.time.month.round)
	.xUnits(d3.time.months)
	.elasticY(true)
	.yAxisLabel("KWh")
	.brushOn(false)
	.renderHorizontalGridLines(true)
	.group(readingGroup)
	.valueAccessor(function(p) {
		if(sensor===0)
			return p.value.count > 0 ? p.value.temp / p.value.count : 0; 
		return p.value.count > 0 ? p.value.light / p.value.count : 0; 
	})
	.title(function(p) {
		var month = (p.key.getMonth())+1;
		var year = (p.key.getYear())+1900;
		var min = numberFormat(p.value.temp / p.value.count);
		if(min==="NaN")
			console.log("NAN")
		var str = "Light level " + p.value.count > 0 ? p.value.light / p.value.count : 0 + " lux\nDate: ";
		if(sensor===0)
			var str = "Temp level " + numberFormat(p.value.count > 0 ? p.value.temp / p.value.count : 0) + " \u00B0C\nDate: ";
		return str + p.key.getDate() + "/" + month + "/" + year;
	});

	dc.renderAll();
	$("#graph").append("<br><hr><br>");
}

function getDate(d, day) {
	var date = d.split("/");
	date[0] = +date[0]+day
	date[1] = date[1]-1;
	if(date[1]===0)
		date[1] = 12;
	var s = new Date(date[2], date[1], date[0]);
	return new Date(date[2], date[1], date[0]);
}

$("#change").onclick(function(d) {
	
	dc.filter();
});