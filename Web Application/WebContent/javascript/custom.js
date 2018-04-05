var lineChart = dc.lineChart("#lineChart");

function startVisual(house) {
	$.post("data", {bName: house},function(data) {
		console.log(data.code);
		switchStatement(data);
	},"json");
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
	var ndx = crossfilter(data),
	parentDimension = ndx.dimension(function (d) {
		return d.room;
	}),
	parentGroup = parentDimension.group().reduceSum(function(d) {
		return d.tem;
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
	.x(d3.scale.linear().domain([0,20]))
	.xUnits(d3.time.months)
	.elasticY(true)
	.yAxisLabel("Test")
	.brushOn(false)
	.renderHorizontalGridLines(true);
	
	dc.renderAll();
}