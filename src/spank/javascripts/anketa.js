var _kk = {};
var _ru = {};

function _init(data) {
    console.log('head_---------------------------------------');
    jQuery("head").empty();
    document.title = 'Анкета – CloudQueue';
    var html = "";
	console.log(data);
	if(data.questions){
		jQuery(data.questions).each(function(i){
			var el = data.questions[i];
			html += "<tr><td colspan='2'><a class='edit editQuestion' data-lane='"+el.id+"'>"+el.question+"</a></td></tr>";                                
			jQuery(el.answersName).each(function(i){
				var el1 = el.answersName[i];
				var el2 = el.answersScore[i];
				html += "<tr><td><p>"+el1.Aname+"</p></td><td><p>"+el2.AScore+"</p></td>";                                				
			});
		});
		jQuery("#questions tbody").html(html);
	}
}

function _onMessage(text){
	console.log("_onMessage:"+text);
	var msg = {};
	try{
		msg = JSON.parse(text);
	}catch(e){
		console.warn("MntrUnt got non-JSON message:"+text);
	}

	if (msg.refresh) {
		console.log("**** REFRESH!!!!");
	}
}

function _onStatus(status){
	if(status=="CONNECTED" || status=="ATTACHED"){
		onConnect();
	}
	if(status=="DISCONNECTED"){
		jQuery(".panel").fadeOut("slow");
	}
}