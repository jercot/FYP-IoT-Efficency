var t;

$(document).ready(function(){

	$("a#dashSet").click(function(e){
		e.preventDefault();
		Android.setWeb("/settings", "Settings");
	});
	
	$("a.dashHou").click(function(e){
		var href = $(this).attr('href').split('=');
		e.preventDefault();
		Android.setWeb("/" + href[0] + "=" + href[1] , href[1]);
	});
	
	if(t!=null) {
		Android.setDevice(t);
	}
	
	

});

function setLocal(str) {
	$("#ips").empty();
	for(var i=0; i<str.length; i++)
		$("#ips").append("<a href=\"http://" + str[i] + ":32109\">" + str[i] + "</a><br>")
}