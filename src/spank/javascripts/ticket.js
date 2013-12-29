var _msg = null;
var _lang = null;
var _id = null;
var timeArray = new Array();
var selectedLane = '';

var _kk = {

	help : "Көмек",
	settings : "Қондырғылар",
	quit : "Шығу",
	close : "Жабу",
	ticket : "Текущие билеты",
	monitoring : "Бақылау",
	line : "Қызмет тізімі",
	number : "Номер",
	waiting_time : 'Время ожидания',
	create_time : "Время распечатки",
	priority : "Приоритет",
	serviced : "Қызмет корсетілген",
	not_came : "Келмеген клиенттер",
	postponed : "Қайта бағытталған",
	operator : "Оператор",
	window : "Терезе",
	call_time : "Шақырылған уақыты",
	start_time : "Басталған уақыты",
	ticket_number : "Билет нөмірі",
	excellent : "Өте жақсы",
	good : "Орташа",
	save : "Сохранить",
	bad : "Нашар",
	average : "Орташа бағасы",
	choose_lang : "Тіл таңдаңыз"
};

var _ru = {
	help : "Помощь",
	settings : "Настройки",
	quit : "Выход",
	ticket : "Текущие билеты",
	monitoring : "Мониторинг",
	waiting_time : 'Время ожидания',
	line : "Линия",
	close : "Закрыть",
	number : "Номер",
	create_time : "Время распечатки",
	priority : "Приоритет",
	not_came : "Клиент не пришел",
	postponed : "Перенаправленные",
	operator : "Оператор",
	window : "Окно",
	call_time : "Время вызова",
	start_time : "Время начала",
	ticket_number : "Номер билета",
	excellent : "Отлично",
	good : "Удовлетворительно",
	bad : "Плохо",
	average : "Средняя оценка",
	save : "Сохранить",
	choose_lang : "Выберите язык"
};

var _en = {
	close : "Close",
	save : "Сохранить",
	help : "Help",
	settings : "Settings",
	quit : "Quit",
	statistics : "Statistics",
	monitoring : "Monitoring",
	waiting_time : 'Waiting time',
	line : "Line",
	number : "Номер",
	create_time : "Время распечатки",
	priority : "Приоритет",
	operator : "Operator",
	window : "Window",
	call_time : "Call time",
	start_time : "Start time",
	ticket_number : "Number of ticket",
	excellent : "Excellent",
	good : "Good",
	bad : "Bad",
	average : "Average mark",
	choose_lang : "Choose language"
};

var _unitStats = {};
var _lanes = new Array();
var _msgBuffer = new Array();
var now = new Date();
var firstTime = 0;

function _init() {
	location.href = "#!tickets";
	// jQuery("head").append(jQuery("<title>CQ - Statistics</title>"));
	document.title = 'CQ - Tickets';
	onConnect();
}
var totalSeconds = 0;
function setTime() {
	jQuery(timeArray).each(function(ix) {
		totalSeconds = ++timeArray[ix];
		// console.log(ix+" "+totalSeconds);
		$('#seconds' + ix).text(pad(totalSeconds % 60));
		$('#minutes' + ix).text(pad(parseInt(totalSeconds % 3600 / 60)));
		$('#hours' + ix).text(pad(parseInt(totalSeconds / 3600)));
	});
	setTimeout(setTime, 1000);
}

function pad(val) {
	var valString = val + "";
	if (valString.length < 2) {
		return "0" + valString;
	} else {
		return valString;
	}
}

jQuery("#logoutLink").click(function() {
	console.log("logoutLink was clicked");
	window.location = "/";
	sessionStorage.clear();
	XmppClient.connection.disconnect();
});

function onConnect() {
	var msg = {
		action : "toTickets"
	};
	XmppClient.send("queue", JSON.stringify(msg));
}

function _onMessage(text) {
	console.log("_onMessage:" + text);
	var msg = {};
	try {
		msg = JSON.parse(text);
	} catch (e) {
		console.warn("unit got non-JSON message:" + text);
		return;
	}

	_lang = msg.lang;
	// console.log(msg);
	if (msg.newticket) {
		now = new Date();
		timeSeconds = now.getHours() * 3600 + now.getMinutes() * 60
				+ now.getSeconds();
		var waitingC = parseInt(msg.newticket.created.substr(0, 2)) * 3600
				+ parseInt(msg.newticket.created.substr(3, 5)) * 60
				+ parseInt(msg.newticket.created.substr(6, 8));
		console.log(timeSeconds);
		console.log(waitingC);
		waiting = timeSeconds - waitingC;
		timeArray[msg.newticket.tokenId] = waiting;

		jQuery("#token-stats tbody").append(
				"<tr data-unit='" + msg.newticket.tokenId + "'>"
						+ "<td><a href='#' onclick='showToken(\""
						+ msg.newticket.tokenId + "\")'>" + msg.newticket.lane
						+ "</a></td>" + "<td data-type='ticket'>"
						+ msg.newticket.ticket + "</td>"
						+ "<td data-type='window'></td>"
						+ "<td data-type='waiting'>" + msg.newticket.created
						+ "</td>" + "<td data-type='called'></td>"
						+ "<td data-type='started'></td>"
						+ "<td data-type='priority'>" + msg.newticket.priority
						+ "</td>" + "<td><span id='start-counter"
						+ msg.newticket.tokenId + "'>" + "<span id='hours"
						+ msg.newticket.tokenId + "'>0</span>:"
						+ "<span id='minutes" + msg.newticket.tokenId
						+ "'>00</span>:" + "<span id='seconds"
						+ msg.newticket.tokenId + "'>00</span>"
						+ "</span></td>" + "</tr>");
		if (_msg != null) {
			_msg.tokens.push(msg.newticket);
		}

		var f = false;
		for ( var i = 0; i < _lanes.length; i++) {
			if (_lanes[i] === msg.newticket.lane) {
				f = true;
				break;
			}
		}
		if (!f) {
			_lanes.push(msg.newticket.lane);
			jQuery('#customDropdown1').append(
					"<option value='" + msg.newticket.lane + "'>"
							+ msg.newticket.lane + "</option>");
		}
		if (selectedLane != '' && selectedLane != msg.newticket.lane) {
			jQuery("tbody tr[data-unit='" + msg.newticket.tokenId + "']")
					.hide();
		}
	} else if (msg.priority) {
		var token = jQuery("tr[data-unit='" + msg.tokenId + "']");
		token.find("td[data-type=priority]").text(msg.priority);
	} else if (_lang) {
		if (_lang == "ru") {
			kk_ru = _ru;
		} else if (_lang == "kk") {
			kk_ru = _kk;
		} else if (_lang == "en") {
			kk_ru = _en;
		}

		jQuery(".settings").empty();
		jQuery("#help").empty();
		jQuery("#logoutLink").empty();
		jQuery("#ticket_title").empty();
		jQuery("#monitoring_title").empty();
		jQuery(".line_title").empty();
		jQuery("#number").text(kk_ru.number);
		jQuery("#create_time").text(kk_ru.create_time);
		jQuery("#priority").text(kk_ru.priority);
		jQuery(".operator_title").empty();
		jQuery("#window").empty();
		jQuery("#call_time").empty();
		jQuery("#start_time").empty();
		jQuery("#ticket_number").empty();
		jQuery("#good").empty();
		jQuery("#bad").empty();
		jQuery('#waiting_time').text(kk_ru.waiting_time);
		jQuery("#average_mark").empty();

		jQuery(".settings").append(kk_ru.settings);
		jQuery(".save").text(kk_ru.save);
		jQuery("#help").append(kk_ru.help);
		jQuery("#logoutLink").append(kk_ru.quit);
		jQuery(".close").text(kk_ru.close);
		jQuery("#ticket_title").append(kk_ru.ticket);
		jQuery("#monitoring_title").append(kk_ru.monitoring);
		jQuery(".line_title").append(kk_ru.line);
		jQuery(".all").append(kk_ru.all);
		jQuery(".reserve").append(kk_ru.reserve);
		jQuery("#serviced").append(kk_ru.serviced);
		jQuery("#not_came").append(kk_ru.not_came);
		jQuery("#postponed").append(kk_ru.postponed);
		jQuery(".operator_title").append(kk_ru.operator);
		jQuery("#window").append(kk_ru.window);
		jQuery("#call_time").append(kk_ru.call_time);
		jQuery("#start_time").append(kk_ru.start_time);
		jQuery("#ticket_number").append(kk_ru.ticket_number);
		jQuery(".excell").text(kk_ru.excellent);
		jQuery("#good").append(kk_ru.good);
		jQuery("#bad").append(kk_ru.bad);
		jQuery("#average_mark").append(kk_ru.average);
	}

	if (msg.dateRepresent) {
		jQuery("#representDate").text(msg.dateRepresent);
	}

	if (msg.username) {
		jQuery("#username").text(msg.username);
	}

	jQuery("#unit-stats tbody").empty();

	if (msg.called) {
		var token = jQuery("tr[data-unit='" + msg.tokenId + "']");
		token.find("td[data-type=called]").text(msg.called);
		token.find("td[data-type=window]").text(msg.window);
	} else if (msg.started) {
		var token = jQuery("tr[data-unit='" + msg.tokenId + "']");
		token.find("td[data-type=started]").text(msg.started);
	} else if (msg.ended) {
		var token = jQuery("tr[data-unit='" + msg.tokenId + "']");
		token.remove();
	}

	if (msg.tokens) {
		jQuery('#customDropdown1').append(
				"<option value=''>Все услуги</option>");

		_msg = msg;
		timeArray = new Array();
		var waiting = 0;
		jQuery(msg.tokens).each(
				function(ix) {
					var el = msg.tokens[ix];
					now = new Date();
					timeSeconds = now.getHours() * 3600 + now.getMinutes() * 60
							+ now.getSeconds();
					var waitingC = parseInt(el.waiting.substr(0, 2)) * 3600
							+ parseInt(el.waiting.substr(3, 5)) * 60
							+ parseInt(el.waiting.substr(6, 8));
					console.log(timeSeconds);
					console.log(waitingC);
					waiting = timeSeconds - waitingC;
					timeArray[el.tokenId] = waiting;

					var start = '';
					var call = '';
					var window = '';
					if (el.start != "null") {
						start = el.start;
					}
					if (el.call != "null") {
						call = el.call;
					}
					if (el.window == el.lane) {
						window = '';
					} else {
						window = el.window;
					}

					jQuery("#token-stats tbody").append(
							"<tr data-unit='" + el.tokenId + "'>"
									+ "<td><a href='#' onclick='showToken(\""
									+ el.tokenId + "\")'>" + el.laneRu
									+ "</a></td>" + "<td data-type='ticket'>"
									+ el.ticket + "</td>"
									+ "<td data-type='window'>" + window
									+ "</td>" + "<td data-type='waiting'>"
									+ el.waiting + "</td>"
									+ "<td data-type='called'>" + call
									+ "</td>" + "<td data-type='started'>"
									+ start + "</td>"
									+ "<td data-type='priority'>" + el.priority
									+ "</td>" + "<td><span id='start-counter"
									+ el.tokenId + "'>" + "<span id='hours"
									+ el.tokenId + "'>0</span>:"
									+ "<span id='minutes" + el.tokenId
									+ "'>00</span>:" + "<span id='seconds"
									+ el.tokenId + "'>00</span>"
									+ "</span></td>" + "</tr>");
					var f = false;
					for ( var i = 0; i < _lanes.length; i++) {
						if (_lanes[i] === el.laneRu) {
							f = true;
							break;
						}
					}
					if (!f) {
						_lanes.push(el.laneRu);
						jQuery('#customDropdown1').append(
								"<option value='" + el.laneRu + "'>"
										+ el.laneRu + "</option>");
					}
				});
		setTime();
	}
}
jQuery(document).ready(function() {
	$('#customDropdown1').change(function() {
		jQuery(this).removeAttr("selected");
		var selected = $(this).find(":selected").text();
		selectedLane = jQuery('#customDropdown1').val();
		console.log(selected);
		changeLanes(selectedLane);
	});
});

function changeLanes(laneName) {
	console.log(laneName);
	if (laneName != '') {
		jQuery("tbody tr").hide();
		jQuery(_msg.tokens).each(function(ix) {
			var el = _msg.tokens[ix]
			if (el.laneRu) {
				if (laneName == el.laneRu) {
					jQuery("tbody tr[data-unit='" + el.tokenId + "']").show();
				}
			} else if (el.lane) {
				if (laneName == el.lane) {
					jQuery("tbody tr[data-unit='" + el.tokenId + "']").show();
				}
			}
		});
	} else {
		jQuery("tr").show();
	}
}
function showToken(id) {
	console.log(id);
	_id = id;
	jQuery(_msg.tokens).each(function(ix) {
		var el = _msg.tokens[ix];
		if (el.tokenId == id) {
			$("#priorityInput").val(el.priority);
			$('#tokenModal').foundation('reveal', 'open');
		}
	});
}

function save() {
	var priority = $("#priorityInput").val();
	console.log(priority);
	$('#tokenModal').foundation('reveal', 'close');
	var msg = {
		action : "editPriority"
	};
	msg.id = _id;
	msg.priority = priority;
	id = null;
	XmppClient.send("queue", JSON.stringify(msg));
}
function goToMonitoring() {
	console.log('gotomonitoring');
	var lang = _lang;
	jQuery("#body").load("/monitor2.html", function(html) {
		_initFromStatistics(lang);
		// _init();
		jQuery("#body").show();
	});
}

function getSettings() {
	jQuery('#messageBody')
			.html(
					'<p>'
							+ kk_ru.choose_lang
							+ '</p>'
							+ '<label for="ru_lang"><input type="radio" id="ru_lang" name="lang" value="ru" />Русский</label>'
							+ '<label for="kk_lang"><input type="radio" id="kk_lang" name="lang" value="kk" />Казахский</label>'
							+ '<label for="en_lang"><input type="radio" id="en_lang" name="lang" value="en" />English</label>');
	$('#message').foundation('reveal', 'open');
}

function change_lang() {
	var inputs = document.getElementsByName("lang");
	for ( var i = 0; i < inputs.length; i++) {
		if (inputs[i].checked) {
			_lang = inputs[i].value;
		}
	}

	if (_lang == "kk") {
		kk_ru = _kk;
	} else if (_lang == "ru") {
		kk_ru = _ru;
	} else if (_lang == "en") {
		kk_ru = _en
	}

	console.log("LANGUAGE IS: " + _lang);
	$('#message').foundation('reveal', 'close');
	XmppClient.send("queue", JSON.stringify({
		action : "changeLang",
		user : sessionStorage.jid,
		language : _lang
	}));
	// locale(_lang);
}

function _onStatus(status) {
	if (status == "CONNECTED" || status == "ATTACHED") {
		onConnect();
	}
	if (status == "DISCONNECTED") {
		console.log("Disconnected");
	}
}
function saveTicketText() {
	var textTicket = kk_ru.operator + ';' + kk_ru.window + ';' + kk_ru.line
			+ ';' + kk_ru.call_time + ';' + kk_ru.start_time + ';'
			+ kk_ru.ticket_number + ';' + kk_ru.all + ';' + kk_ru.reserve + ';'
			+ kk_ru.excellent + ';' + kk_ru.good + ';' + kk_ru.bad + ';'
			+ kk_ru.average + "\n";
	jQuery(msg.tokens).each(
			function(ix) {
				var el = msg.tokens[ix];
				var start = '';
				var call = '';
				var window = '';
				if (el.start != "null") {
					start = el.start;
				}
				if (el.call != "null") {
					call = el.call;
				}
				if (el.window == el.lane) {
					window = '';
				} else {
					window = el.window;
				}

				textTicket += el.laneRu + ";" + el.ticket + ";" + window + ";"
						+ el.waiting + ";" + call + ";" + start + ";"
						+ el.priority + ";" + $('#hours' + el.tokenId).text()
						+ " " + $('#minutes' + el.tokenId).text() + " "
						+ $('#second' + el.tokenId).text() + "\n";
			});
	var blob = new Blob([ textTicket ], {
		type : "text/plain;charset=Unicode",
	});
	saveAs(blob, "monitor_ticket.csv");
	return false;
}
