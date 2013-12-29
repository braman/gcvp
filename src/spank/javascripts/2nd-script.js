var second_step_1 = $(".second-step-1"), second_number_1 = $(".second-number"), second_step_2 = $(".second-step-2"), second_step_3 = $(".second-step-3"), second_step_4 = $(".second-step-4"), second_step_5 = $(".second-step-5"), second_step_6 = $(".second-step-6"), second_footer = $(".second-footer");

var remote_second = {
	step1 : function() {
		second_step_2.add(second_step_3).add(second_step_4).add(second_step_5)
				.add(second_step_6).add(second_footer).fadeOut().promise()
				.done(function() {
					second_step_1.fadeIn();
				});
	},
	step2 : function() {
		second_step_1.add(second_step_3).add(second_step_4).add(second_step_5)
				.add(second_step_6).fadeOut().promise().done(function() {
					second_step_2.add(second_footer).fadeIn();
				});
	},
	step3 : function() {
		second_step_1.add(second_step_2).add(second_step_4).add(second_step_5)
				.add(second_step_6).fadeOut().promise().done(function() {
					second_step_3.add(second_footer).fadeIn();
				});
	},
	step4 : function() {
		second_step_1.add(second_step_2).add(second_step_3).add(second_step_5)
				.add(second_step_6).add(second_footer).fadeOut().promise()
				.done(function() {
					second_step_4.fadeIn();
				});
	},
	step5 : function() {
		second_step_1.add(second_step_2).add(second_step_3).add(second_step_4)
				.fadeOut().promise().done(function() {
					second_step_5.add(second_footer).fadeIn();
				});
	},
	step6 : function() {
		second_step_1.add(second_step_2).add(second_step_3).add(second_step_4)
				.fadeOut().promise().done(function() {
					second_step_6.add(second_footer).fadeIn();
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
		second_step_6.fadeOut();
		second_step_5.fadeOut();
		second_step_4.fadeOut();
		second_step_3.fadeOut();
		second_footer.fadeOut();
		$('#ok_button').fadeOut();
		sessionStorage.setItem('step','back');
		/*
		 * jQuery('body').load("/secondMonitor/smile.html",function(html){
		 * jQuery('#smileOutput').css("height", (window.outerHeight-62)+"px");
		 * jQuery('#smileOutput').css("width", window.outerWidth+"px");
		 * jQuery('#smileOutput').css("margin", "0 auto"); });
		 */
	}
	if (msg.refresh) {
		console.log("**** REFRESH!!!!");
		second_number_1.empty();
	}

	if (msg.refreshSmile) {
		console.log("**** REFRESH SMILE!!!!");

		/*
		 * jQuery('body').load("/secondMonitor/ticket.html",function(html){
		 * jQuery('#ticketOutput').html("Спасибо Вам!");
		 * 
		 * jQuery('#ticketOutput').css("height", (window.outerHeight-62)+"px");
		 * jQuery('#ticketOutput').css("width", window.outerWidth+"px");
		 * jQuery('#ticketOutput').css("font-size", "160px");
		 * jQuery('#ticketOutput').css("margin", "0 auto");
		 * 
		 * setTimeout(refresh, 4000); });
		 */
	}
	if (msg.video) {
		myVideo();
		sessionStorage.setItem('step','video');
		/*
		 * jQuery('body').load("/secondMonitor/anketa.html",function(html){
		 * jQuery('#advertisementOutput').css("height",
		 * (window.outerHeight-62)+"px");
		 * jQuery('#advertisementOutput').css("width", window.outerWidth+"px");
		 * jQuery('#advertisementOutput').css("margin", "0 auto"); });
		 */
	}
	if (msg.advertisement) {
		console.log("**** ADVERTISEMENT!!!!");
		toAnketa();
		Qnumber = 0;
		/*
		 * jQuery('body').load("/secondMonitor/anketa.html",function(html){
		 * jQuery('#advertisementOutput').css("height",
		 * (window.outerHeight-62)+"px");
		 * jQuery('#advertisementOutput').css("width", window.outerWidth+"px");
		 * jQuery('#advertisementOutput').css("margin", "0 auto"); });
		 */
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
	second_step_4.fadeOut();
	second_step_2.fadeOut();
	$('#ok_button').fadeOut();
	$('.second-step-7').fadeOut();
	second_step_3.add(second_footer).fadeIn();
}
function myVideo(){
	console.log("**** video !!!!");
	second_step_1.fadeOut();
	$('#ok_button').fadeOut();
	$('.second-step-7').fadeOut();
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
	second_step_6.fadeOut();
	second_step_5.fadeOut();
	second_step_4.fadeOut();
	second_step_3.fadeOut();
	second_footer.fadeOut();
}
function bot() {
	setInterval(function() {
		if (_smile) {
			_smile = false;
			var t = parseInt(Math.random() * 100);
			if (t > 60) {
				smile(5);
				console.log('good');
			} else if (t < 30) {
				smile(3);
				console.log('norm');
			} else {
				smile(2);
				console.log('bad');
			}
		}
	}, 2000);
}

var aname = "";
function toAnketa(aname) {
	console.log(Qnumber);
	console.log(_data.questions.length);
	if (aname) {
		Qnumber++;
	}
	if (Qnumber >= _data.questions.length) {
		toAnketaThanks();
		var msg = {
			action : "user_anketa",
			answer : aname
		};
		XmppClient.send("queue", JSON.stringify(msg));
	} else {
		if (Qnumber > 0) {
			var msg = {
				action : "user_anketa",
				answer : aname
			};
			XmppClient.send("queue", JSON.stringify(msg));
		}
		// var rand = Math.floor(Math.random() * questions.length);
		// console.log(rand);
		$('#anketa').empty();
		$('#ad').empty();
		$('#anketa').append(
				$("<h5 class='question'>" + _data.questions[Qnumber].question
						+ "</h5>"));
		$(_data.questions[Qnumber].answersName)
				.each(
						function(i) {
							if (_data.questions[Qnumber].answersName[i].Aname != '')
								$('#anketa')
										.append(
												$("<a class='feedback-link answer' onclick=\"toAnketa('"
														+ _data.questions[Qnumber].answersId[i].AId
														+ "');\">"
														+ _data.questions[Qnumber].answersName[i].Aname
														+ "</a>"));
						});

		second_step_1.fadeOut();
		second_step_2.fadeOut();
		second_step_3.fadeOut();
		$('.second-step-7').fadeOut();
		$('#ok_button').fadeOut();
		second_step_4.fadeIn();
	}
}

function toNumbers(content) {
	global_content = content;
	console.log("########################################################");
	// $("#numbersDiv").text(JSON.stringify(obj) + " " + content.number0);
	$('#numberUl').empty();
	$(content.number)
			.each(
					function(i) {
						console
								.log("*******************************************************");
						$('#numberUl')
								.append(
										"<li style='display: block;'><a style='padding-bottom: 0px; padding-top: 0px;' id='"
												+ content.number[i].substring(
														1, 12)
												+ "' class='kiosk-link block' onclick=\" toggleNumber('"
												+ content.number[i]
												+ "')\">"
												+ "<span>"
												+ content.number[i]
												+ "</span></a></li>");
					});

	second_step_1.fadeOut();
	second_step_2.fadeOut();
	second_step_3.fadeOut();
	second_step_4.fadeOut();
	second_step_5.fadeOut();
	second_step_6.fadeOut();
	$('.second-step-7').fadeIn();
	$('#ok_button').fadeIn();
}

function toggleNumber(number) { // laneTypeName
	kiosk_services = $("#kiosk-services a");
	IDNumber = $("#" + number.substring(1, 12));

	var $this = IDNumber;

	if (!$this.hasClass('active')) {
		$this.addClass('active');

		$(global_content.number).each(
				function(i) {
					if (global_content.number[i] != number) {
						$("#" + global_content.number[i].substring(1, 12))
								.removeClass('active');
					}
				});
	} else
		$this.removeClass('active');

	return false;
}

function send_number() {
	$(global_content.number).each(
			function(i) {
				if($("#" + global_content.number[i].substring(1, 12)).hasClass('active')){
					var msg = {
							action : "send_number",
							number : global_content.number[i],
							receiver: _sender
						};
						XmppClient.send("queue", JSON.stringify(msg));
				}
			});
}

function toAnketaThanks() {
	second_step_3.fadeOut();
	second_step_4.fadeOut();
	$('.second-step-7').fadeOut();
	$('#ok_button').fadeOut();
	second_step_6.fadeIn();

	setTimeout(function() {
		toVideo();
	}, 10000);
}

function toVideo() {
	second_step_4.fadeOut();
	second_step_6.fadeOut();
	$('.second-step-7').fadeOut();
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
	$('.second-step-7').fadeOut();
	second_step_5.fadeIn();
	var msg = {
		action : "ticket_smile",
		token : tokenId,
		condition : cond
	};
	XmppClient.send("queue", JSON.stringify(msg));
}