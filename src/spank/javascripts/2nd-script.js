var second_step_1 = $(".second-step-1"), second_number_1 = $(".second-number"), second_step_2 = $(".second-step-2"), second_step_3 = $(".second-step-3"), second_step_5 = $(".second-step-5"), second_footer = $(".second-footer");

var remote_second = {
	step1 : function() {
		second_step_2.add(second_step_3).add(second_step_5)
				.add(second_footer).fadeOut().promise()
				.done(function() {
					second_step_1.fadeIn();
				});
	},
	step2 : function() {
		second_step_1.add(second_step_3).add(second_step_5)
				.fadeOut().promise().done(function() {
					second_step_2.add(second_footer).fadeIn();
				});
	},
	step3 : function() {
		second_step_1.add(second_step_2).add(second_step_5)
				.fadeOut().promise().done(function() {
					second_step_3.add(second_footer).fadeIn();
				});
	},
	step5 : function() {
		second_step_1.add(second_step_2).add(second_step_3)
				.fadeOut().promise().done(function() {
					second_step_5.add(second_footer).fadeIn();
				});
	}
};

var _kk = {
	very_good : "Өте жақсы",
	good : "Жақсы",
	normal : "Нашар емес",
	bad : "Нашар",
	very_bad : "Өте нашар",
	anketa : "Анкета",
	thanks_for_time : "Уақытыңызға рахмет!",
	thanks : "Рахмет!",
	opinion : "Қызмет атқарудың деңгейі туралы Сіздің пікіріңіз"
};
var _ru = {
	very_good : "Супер!",
	good : "Отлично",
	normal : "Хорошо",
	bad : "Не очень",
	very_bad : "Плохо",
	anketa : "Анкета",
	thanks_for_time : "Спасибо за ваше время!",
	thanks : "Спасибо!",
	opinion : "Ваше мнение об уровне услуг"
};

var _kk_ru;
var _lang = "";

var iinFake;

var _sender = "";
var kkButton;
var ruButton;
var tokenId;
var _data = null;
var Qnumber = 0;
var _smile = false;
var global_content = '';
function _init(data) {
	console.log('head_---------------------------------------');
	jQuery("head").empty();
	document.title = 'Второй монитор – CloudQueue';
	_data = data.group;
	var array = new Array();
	var first = true;
	$('#ad').empty();
	$('#ok_button').fadeOut();
	setTimeout(function () {
		if(sessionStorage.getItem('step')=='ticket'){
			tokenId = sessionStorage.getItem('tokenId');
			myTicket(sessionStorage.getItem('tokenId'),sessionStorage.getItem('code'));			 
		}else if(sessionStorage.getItem('step')=='smile'){
			_lang = sessionStorage.getItem('lang')
			mySmile();
		}else if(sessionStorage.getItem('step')=='video'){
			myVideo();
		} 
    }, 1000);
	
	console.log(data);
}

function _onMessage(text) {
	console.log("_onMessage:" + text);
	var msg = {};
	try {
		msg = JSON.parse(text);
	} catch (e) {
		console.warn("MntrUnt got non-JSON message:" + text);
	}

	if (msg.smile) {
		_lang = msg.lang;
		console.log(msg);
		mySmile();
		sessionStorage.setItem('step','smile');
		sessionStorage.setItem('lang', msg.lang);
	}
	if (msg.back) {
		console.log("**** Back!!!!");
		console.log(msg);

		second_number_1.hide();
		second_step_1.fadeOut();
		second_step_5.fadeOut();
		second_step_3.fadeOut();
		second_footer.fadeOut();
		$('#ok_button').fadeOut();
		sessionStorage.setItem('step','back');
	}
	if (msg.refresh) {
		console.log("**** REFRESH!!!!");
		second_number_1.empty();
	}

	if (msg.refreshSmile) {
		console.log("**** REFRESH SMILE!!!!");
	}
	if (msg.video) {
		myVideo();
		sessionStorage.setItem('step','video');
	}

	if (msg.numbers) {
		console.log("Numbers!!!!!!!!!!!");
		console.log(msg.content);
		console.log(msg.sender);
		_sender = msg.sender;
		if (msg.content != null)
			toNumbers(msg.content);
	}

	if (msg.ticket) {
		tokenId = msg.ticket.tokenId;
		myTicket(msg.ticket.tokenId,msg.ticket.code);
		sessionStorage.setItem('step','ticket');
		sessionStorage.setItem('tokenId',msg.ticket.tokenId);
		sessionStorage.setItem('code',msg.ticket.code);
	}
}
function mySmile(){
	console.log("**** SMILE!!!!");
	_smile = true;
	$('#ad').empty();
	if (_lang == "KK")
		_kk_ru = _kk;
	else if (_lang == "RU")
		_kk_ru = _ru;
	else if (_lang == "EN")
		_kk_ru = _ru;
	
	
	if (_kk_ru != null || _kk_ru != "") {
		jQuery("#very_good").text(_kk_ru.very_good);
		jQuery("#good").text(_kk_ru.good);
		jQuery("#normal").text(_kk_ru.normal);
		jQuery("#bad").text(_kk_ru.bad);
		jQuery("#very_bad").text(_kk_ru.very_bad);
		jQuery("#opinion").text(_kk_ru.opinion);
		jQuery("#anketa").text(_kk_ru.anketa);
		jQuery("#thanks_for_time").text(_kk_ru.thanks_for_time);
		jQuery("#thanks").text(_kk_ru.thanks);
	}
	second_number_1.empty();
	second_step_2.fadeOut();
	$('#ok_button').fadeOut();
	second_step_3.add(second_footer).fadeIn();
}
function myVideo(){
	console.log("**** video !!!!");
	second_step_1.fadeOut();
	$('#ok_button').fadeOut();
	$('#ad').html("<img src='images/logo/gcvp.jpg' style='width:100%'>");
	console.log('myVideo');
	second_step_2.add(second_footer).fadeIn();
	console.log('myVideoFinished');
}
function myTicket(tokenId,code){
	console.log("**** TICKET!!!!");
	$('#ad').empty();
	tokenId = tokenId;
	second_number_1.text(code);
	second_number_1.show();
	second_step_1.fadeIn();
	second_step_5.fadeOut();
	second_step_3.fadeOut();
	second_footer.fadeOut();
}

var aname = "";

function toVideo() {
	$('#ok_button').fadeOut();
	$('#ad').html("<img src='images/logo/gcvp.jpg' style='width:100%'>");
	second_step_2.add(second_footer).fadeIn();
}

function refresh() {
	second_number_1.empty();
}

function _onStatus(status) {
	if (status == "CONNECTED" || status == "ATTACHED") {
		onConnect();
	}
	if (status == "DISCONNECTED") {
		jQuery(".panel").fadeOut("slow");
	}
}

function smile(cond) {
	second_step_3.fadeOut();
	$('#ok_button').fadeOut();
	second_step_5.fadeIn();
	var msg = {
		action : "ticket_smile",
		token : tokenId,
		condition : cond
	};
	XmppClient.send("queue", JSON.stringify(msg));
}