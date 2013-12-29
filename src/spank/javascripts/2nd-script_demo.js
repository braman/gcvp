var _kk = {};
var _ru = {};

var kkButton;
var ruButton;
var tokenId;
var firstTime = 0;
var people = null;
var index = 0;
var ticketCode = '000';
var demoCounter = $('#demo_counter');

function _init(data) {
    console.log('head_---------------------------------------');
    jQuery("head").empty();
    document.title = 'CQ Demo';

	console.log(data);

	console.log('init astex');
	demoCounter.hide();
	if(navigator.webkitGetUserMedia)
	{
		navigator.webkitGetUserMedia({video:true}, onSuccess, onFail);
	}
	else
	{
		alert('webRTC not available');
	}
}

function onSuccess(stream)
{
	document.getElementById('camFeed').src = webkitURL.createObjectURL(stream);
}

function onFail()
{
	alert('could not connect stream');
}

function _onMessage(text){

	console.log("_onMessage:"+text);
	var msg = {};
	try{
		msg = JSON.parse(text);
	}catch(e){
		console.warn("MntrUnt got non-JSON message:"+text);
	}

	if (msg.smile) {
		console.log("**** SMILE!!!!");
		console.log('end');
//		remote.aigerim.end();
		people[index++].end(ticketCode);
		console.log(msg);
		
		
		/*
		jQuery('body').load("/secondMonitor/smile.html",function(html){
			jQuery('#smileOutput').css("height", (window.outerHeight-62)+"px");
			jQuery('#smileOutput').css("width", window.outerWidth+"px");
			jQuery('#smileOutput').css("margin", "0 auto");
		});
		*/
	}

	if (msg.refresh) {
		console.log("**** REFRESH!!!!");
	}

	if (msg.refreshSmile) {
		console.log("**** REFRESH SMILE!!!!");
		/*
		jQuery('body').load("/secondMonitor/ticket.html",function(html){
			jQuery('#ticketOutput').html("Спасибо Вам!");

			jQuery('#ticketOutput').css("height", (window.outerHeight-62)+"px");
			jQuery('#ticketOutput').css("width", window.outerWidth+"px");
			jQuery('#ticketOutput').css("font-size", "160px");
			jQuery('#ticketOutput').css("margin", "0 auto");

			setTimeout(refresh, 4000);
		});
		*/
	}
	if (msg.video) {
		console.log("**** video !!!!");
		console.log('stay');
		people[index++].stay(ticketCode);
		//remote.aigerim.stay();
		console.log(msg);
		
		/*
		jQuery('body').load("/secondMonitor/anketa.html",function(html){
			jQuery('#advertisementOutput').css("height", (window.outerHeight-62)+"px");
			jQuery('#advertisementOutput').css("width", window.outerWidth+"px");
			jQuery('#advertisementOutput').css("margin", "0 auto");
		});
		*/
	}
	if (msg.advertisement) {
		console.log("**** ADVERTISEMENT!!!!");
		console.log(msg);
		/*
		jQuery('body').load("/secondMonitor/anketa.html",function(html){
			jQuery('#advertisementOutput').css("height", (window.outerHeight-62)+"px");
			jQuery('#advertisementOutput').css("width", window.outerWidth+"px");
			jQuery('#advertisementOutput').css("margin", "0 auto");
		});
		*/
	}
	
	if (msg.ticket) {
		if(firstTime==0){
			firstTime++;
			jQuery("body").append(jQuery("<script type='text/javascript' src='finished/jquery-seeThru.js'></script>"));
			people = [remote.aigerim,remote.aigerim,remote.aigerim,
			          remote.gulsaya,remote.gulsaya,remote.gulsaya,
			          remote.rahilya,remote.rahilya,remote.rahilya,
			          remote.nurlan,remote.nurlan,remote.nurlan,
			          remote.eldar,remote.eldar,remote.eldar];
			index = 0;
	    }
		if(index==15){
			index==0;
		}
		console.log("**** TICKET!!!!");
		console.log(index);
		ticketCode = msg.ticket.code;
		people[index++].call(ticketCode);
		
//		remote.aigerim.call();
		
		console.log(msg.ticket);

		tokenId = msg.ticket.tokenId;
	}
}


function refresh() {
}

function _onStatus(status){
	if(status=="CONNECTED" || status=="ATTACHED"){
		onConnect();
	}
	if(status=="DISCONNECTED"){
		jQuery(".panel").fadeOut("slow");
	}
}

function smile(cond) {
	var msg = {action:"ticket_smile",token:tokenId,condition:cond};
	XmppClient.send("queue",JSON.stringify(msg));
}