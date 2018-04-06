var lineChart = dc.lineChart("#lineChart");

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
		createGraph(data.records);
		break;
	}
}

function createGraph(data) {
	data.forEach(function (d) {
		var date = new Date(+d.ti);
        d.month = d3.time.month(date);
        d.week = d3.time.week(date);
        d.day = d3.time.day(date);
        d.hour = d3.time.hour(date);
    });
	var ndx = crossfilter(data),
	parentDimension = ndx.dimension(function (d) {
		return d.te;
	}),
	parentGroup = parentDimension.group().reduceSum(function(d) {
		return 1;
	});

	lineChart
	.renderArea(true)
	.width(990)
	.height(200)
	.transitionDuration(1000)
	.margins({top: 0, right: 50, bottom: 25, left: 40})
	.dimension(parentDimension)
	.group(parentGroup)
	.round(d3.time.month.round)
	.x(d3.scale.linear().domain([13,21]))
	.xUnits(d3.time.months)
	.elasticY(true)
	.yAxisLabel("Test")
	.brushOn(false)
	.renderHorizontalGridLines(true);
	
	dc.renderAll();
	$("#graph").append("<br><hr><br>");
}