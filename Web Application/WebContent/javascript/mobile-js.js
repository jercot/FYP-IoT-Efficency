$(document).ready(function() {
	$("a#dashSet").click(function(e){
		e.preventDefault();
		Android.setWeb("/settings", "Settings");
	});
	
	$("a.dashHou").click(function(e){
		var href = $(this).attr('href').split('=');
		e.preventDefault();
		Android.setWeb("/" + href[0] + "=" + href[1] , href[1]);
	});
});

function setLocal(str) {
	$("#ips").empty();
	for(var i=0; i<str.length; i++)	{
		if(str[i].code==1) {
			var t = $("#" + str[i].room);
			t.append('Sensor connected at octet: ' + str[i].octet);	
		}
		else
			$("#sensorList").append('Unset sensor at octet: ' + str[i].octet);
	}
}