var _kk = {
	help : "Көмек",
	settings : "Қондырғылар",
	quit : "Шығу",
	monitoring : "Бақылау",
	go_to_statistics : "Санаққа өту",
	go_to_tickets : "Текущие билеты",
	line : "Қызмет тізімі",
	all : "Барлығы",
	reserve : "Бронь",
	serviced : "Қызмет корсетілген",
	notcame : "Келмеген клиенттер",
	routed : "Қайта бағытталған",
	operator : "Оператор",
	window : "Терезе",
	close : "Закрыть",
	waiting_time : 'Время ожидания',
	create_time : "Время распечатки",
	
	call_time : "Шақырылған уақыты",
	start_time : "Басталған уақыты",
	ticket_number : "Билет нөмірі",
	excellent : "Өте жақсы",
	priority : "Приоритет",
	norm : "Орташа",
	good : "Жақсы",
	bad : "Жаман",
	save : "Сохранить",
	veryBad : "Өте жаман",
	average : "Орташа бағасы",
	history : "Тарихы",
	ticket : "Билет",
	start : "Басы",
	end : "Соңы",
	mark : "Бағасы",
	choose_lang : "Тіл таңдаңыз"
};
var _ru = {
	help : "Помощь",
	waiting_time : 'Время ожидания',
	settings : "Настройки",
	quit : "Выход",
	monitoring : "Мониторинг",
	go_to_statistics : "Перейти на статистику",
	go_to_tickets : "Текущие билеты",
	line : "Услуги",
	all : "Всего",
	reserve : "Бронь",
	serviced : "Обслужено",
	notcame : "Клиент не пришел",
	routed : "Перенаправленные",
	operator : "Оператор",
	window : "Окно",
	call_time : "Время вызова",
	start_time : "Время начала",
	ticket_number : "Номер билета",
	close : "Закрыть",
	
	// ticket.js
	number : "Номер",
	create_time : "Время распечатки",
	priority : "Приоритет",
	waiting_time : 'Время ожидания',
	save : "Сохранить",
	
	good : "Хорошо",
	veryBad : "Очень плохо",
	excellent : "Отлично",
	norm : "Удовлетворительно",
	bad : "Плохо",

	average : "Средняя оценка",
	history : "История",
	ticket : "Билет",
	start : "Начало",
	end : "Конец",
	mark : "Оценка",
	choose_lang : "Выберите язык"
};


var _unitStats = {};
// var _unitStatistics = {};
var _laneStats = {};
var _lanes = {};
var _msgBuffer = new Array();
var _lang = null;
var kk_ru = _ru;
var _msgBuffer = new Array();

function _init(data) {
	location.href = "#!monitor";
	jQuery("head").append(
			jQuery("<link rel='stylesheet' href='stylesheets/app.min.css' />"));
	document.title = 'CQ - Monitor';

	if (data.lang) {
		_lang = data.lang;

		var translated = "";
		console.log("Lang: " + _lang);

		if (_lang == "kk") {
			kk_ru = _kk;
		} else if (_lang == "ru") {
			kk_ru = _ru;
		} else if (_lang == "en") {
			kk_ru = _en;
		}

		jQuery("#help").append(kk_ru.help);
		jQuery(".settings").append(kk_ru.settings);
		jQuery("#logoutLink").append(kk_ru.quit);
		jQuery("#go_to_statistics").append(kk_ru.go_to_statistics);
		jQuery("#go_to_tickets").text(kk_ru.go_to_tickets);
		jQuery(".close").text(kk_ru.close);
		jQuery(".line").append(kk_ru.line);
		jQuery(".all").text(kk_ru.all);
		jQuery(".reserve").text(kk_ru.reserve);
		jQuery(".serviced").text(kk_ru.serviced);
		jQuery(".not_came").text(kk_ru.notcame);
		jQuery(".routed").text(kk_ru.routed);
		jQuery(".operator").append(kk_ru.operator);
		jQuery(".window").append(kk_ru.window);
		jQuery(".call_time").append(kk_ru.call_time);
		jQuery(".start_time").append(kk_ru.start_time);
		jQuery("#ticket_number").text(kk_ru.ticket_number);
		jQuery(".excell").text(kk_ru.excellent);
		jQuery(".good").text(kk_ru.good);
		jQuery(".norm").text(kk_ru.norm);
		jQuery(".veryBad").text(kk_ru.veryBad);
		jQuery(".bad").text(kk_ru.bad);
		jQuery("#average").append(kk_ru.average);
		jQuery(".history").append(kk_ru.hi);
		jQuery(".ticket").append(kk_ru.ticket);
		jQuery(".start").append(kk_ru.start);
		jQuery(".end").append(kk_ru.end);
		jQuery("#monitoring").text(kk_ru.monitoring);
		jQuery(".mark").append(kk_ru.mark);
		// ticket.js
		jQuery("#number").text(kk_ru.number);
		jQuery("#create_time").text(kk_ru.create_time);
		jQuery("#priority").text(kk_ru.priority);
		jQuery('#waiting_time').text(kk_ru.waiting_time);
	}

	jQuery('.error').hide();

	onConnect();
}

function onConnect() {
	XmppClient.send("queue", "init");
}

function _initFromLocalAdmin(lang){
	console.log(lang);
	location.href = "#!monitor";
	document.title = 'CQ - Monitor';
	
	jQuery('.error').hide();

		_lang = lang;

		var translated = "";
		console.log("Lang: " + _lang);
		
		if(_lang=="kk"){
			kk_ru = _kk;
		}
		else{
			kk_ru = _ru;
		}
		
		jQuery("#help").append(kk_ru.help);
		jQuery(".settings").append(kk_ru.settings);
		jQuery("#logoutLink").append(kk_ru.quit);
		jQuery("#go_to_statistics").append(kk_ru.go_to_statistics);
		jQuery("#go_to_tickets").text(kk_ru.go_to_tickets);
		jQuery(".line").append(kk_ru.line);
		jQuery(".all").append(kk_ru.all);
		jQuery(".reserve").append(kk_ru.reserve);
		jQuery("#serviced").append(kk_ru.serviced);
		jQuery("#not_came").append(kk_ru.notcame);
		jQuery("#monitoring").text(kk_ru.monitoring);
		jQuery("#routed").append(kk_ru.routed);
		jQuery(".operator").append(kk_ru.operator);
		jQuery("#window").append(kk_ru.window);
		jQuery("#call_time").append(kk_ru.call_time);
		jQuery("#start_time").append(kk_ru.start_time);
		jQuery("#ticket_time").append(kk_ru.ticket_number);
		jQuery("#excell").append(kk_ru.excellent);
		jQuery("#good").append(kk_ru.good);
		jQuery("#bad").append(kk_ru.bad);
		jQuery("#average").append(kk_ru.average);
		jQuery(".history").append(kk_ru.hi);
		jQuery(".ticket").append(kk_ru.ticket);
		jQuery(".start").append(kk_ru.start);
		jQuery(".end").append(kk_ru.end);
		jQuery(".mark").append(kk_ru.mark);


	
	onConnect();
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

	if (msg.username) {
		jQuery("#username").text(msg.username);
	}

	if (msg.laneStats) {
		_laneStats = msg.laneStats;
		updateLaneStats();
	}
	if (msg.unitStats) {
		_unitStats = msg.unitStats;

		jQuery("#unit-stats tbody").empty();
		jQuery(_unitStats).each(
				function(ix) {
					var el = _unitStats[ix];

					jQuery("#unit-stats tbody").append(
							"<tr data-unit='" + el.username + "'>"
									+ "<td><a href='#' onclick='showUnit(\""
									+ el.username + "\")'>" + el.firstname
									+ " " + el.lastname + "</a></td>" + "<td>"
									+ el.window + "</td>"
									+ "<td data-stat='tokenAll'>" + el.tokenAll
									+ "</td>"
									+ "<td data-stat='countReserved'>"
									+ el.countReserved + "</td>"
									+ "<td data-stat='countMarkGood'>"
									+ el.countMarkBest + "</td>"
									+ "<td data-stat='countMarkNorm'>"
									+ el.countMarkGood + "</td>"
									+ "<td data-stat='countReserved'>"
									+ el.countMarkNorm + "</td>"
									+ "<td data-stat='countMarkGood'>"
									+ el.countMarkBad + "</td>"
									+ "<td data-stat='countMarkBad'>"
									+ el.countMarkVeryBad + "</td>"
									+ "<td data-stat='markAverage'>"
									+ el.markAverage + "</td>" + "</tr>");
				});

		jQuery("#unit-mon tbody").empty();
		jQuery("#mark-stats tbody").empty();
		jQuery(_unitStats)
				.each(
						function(ix) {
							var el = _unitStats[ix];
							jQuery("#unit-mon tbody")
									.append(
											"<tr data-unit='"
													+ el.username
													+ "'>"
													+ "<td><a href='#' onclick='showUnit(\""
													+ el.username
													+ "\")'>"
													+ el.firstname
													+ " "
													+ el.lastname
													+ "</a></td>"
													+ "<td>"
													+ el.window
													+ "</td>"
													+ "<td data-type='lane'>"
													+ (_lanes[el.lane] != undefined ? _lanes[el.lane]
															: "")
													+ "</td>"
													+ "<td data-type='called'>"
													+ (el.called != undefined ? el.called
															: "")
													+ "</td>"
													+ "<td data-type='started'>"
													+ (el.started != undefined ? el.started
															: "")
													+ "</td>"
													+ "<td data-type='ticket'>"
													+ (el.ticketCode != undefined ? el.ticketCode
															: "")
													+ "</td>"
													+ "<td data-stat='tokenAll'>"
													+ el.tokenAll
													+ "</td>"
													+ "<td data-stat='countReserved'>"
													+ el.countReserved
													+ "</td>" + "</tr>");

							jQuery("#mark-stats tbody").append(
									"<tr data-unit='" + el.username + "'>"
											+ "<td><a href='#' >"
											+ el.firstname + " " + el.lastname
											+ "</a></td>"
											+ "<td data-stat='countMarkGood'>"
											+ el.countMarkBest + "</td>"
											+ "<td data-stat='countMarkNorm'>"
											+ el.countMarkGood + "</td>"
											+ "<td data-stat='countReserved'>"
											+ el.countMarkNorm + "</td>"
											+ "<td data-stat='countMarkGood'>"
											+ el.countMarkBad + "</td>"
											+ "<td data-stat='countMarkBad'>"
											+ el.countMarkVeryBad + "</td>"
											+ "<td data-stat='markAverage'>"
											+ el.markAverage + "</td>"
											+ "</tr>");
						});
	}
	if (_laneStats.length == undefined || _unitStats.length == undefined) {
		_msgBuffer.push(msg);
		return;
	} else {
		if (_msgBuffer.length != 0) {
			jQuery(_msgBuffer).each(function(ix) {
				processMessage(_msgBuffer[ix]);
			});
			_msgBuffer = new Array();
		}
	}
	processMessage(msg);
	// statistics
	if (msg.dateRepresent) {
		jQuery("#representDateLane").text(msg.dateRepresent);
		jQuery("#representDateUnit").text(msg.dateRepresent);
	}

	// ticket.js

	_lang = msg.lang;
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
						+ msg.newticket.tokenId + "\")'>" + msg.newticket.laneRu
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

		if (_msg != null && _msg.tokens != null) {
			_msg.tokens.push(msg.newticket);
		}

		// var f = false;
		// for ( var i = 0; i < _lanes.length; i++) {
		// if (_lanes[i] === msg.newticket.lane) {
		// f = true;
		// break;
		// }
		// }
		// if (!f) {
		// _lanes.push(msg.newticket.lane);
		// jQuery('#customDropdown1').append(
		// "<option value='" + msg.newticket.lane + "'>"
		// + msg.newticket.lane + "</option>");
		// }
		if (selectedLane != '' && selectedLane != msg.newticket.lane) {
			jQuery("tbody tr[data-unit='" + msg.newticket.tokenId + "']")
					.hide();
		}
	} else if (msg.priority) {
		var token = jQuery("tr[data-unit='" + msg.tokenId + "']");
		token.find("td[data-type=priority]").text(msg.priority);
	} else if (_lang) {
		if (_lang == "kk") {
			kk_ru = _kk;
		}else{
			kk_ru = _ru;
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
		jQuery('#waiting_time').text(kk_ru.waiting_time);
		jQuery("#average_mark").empty();
		jQuery(".close").text(kk_ru.close);
		
		jQuery(".settings").append(kk_ru.settings);
		jQuery(".save").text(kk_ru.save);
		jQuery("#help").append(kk_ru.help);
		jQuery("#logoutLink").append(kk_ru.quit);
		jQuery(".close").text(kk_ru.close);
		jQuery("#ticket_title").append(kk_ru.ticket);
		jQuery("#monitoring_title").append(kk_ru.monitoring);
		jQuery(".line_title").append(kk_ru.line);
		jQuery(".all").text(kk_ru.all);
		jQuery(".reserve").text(kk_ru.reserve);
		jQuery(".serviced").text(kk_ru.serviced);
		jQuery(".not_came").text(kk_ru.not_came);
		jQuery(".postponed").text(kk_ru.postponed);
		jQuery(".operator_title").append(kk_ru.operator);
		jQuery("#window").append(kk_ru.window);
		jQuery("#call_time").append(kk_ru.call_time);
		jQuery("#start_time").append(kk_ru.start_time);
		jQuery("#ticket_number").append(kk_ru.ticket_number);
		jQuery(".excell").text(kk_ru.excellent);
		jQuery("#good").text(kk_ru.good);
		jQuery(".bad").text(kk_ru.bad);
		jQuery("#average_mark").append(kk_ru.average);
	}

	if (msg.dateRepresent) {
		jQuery("#representDate").text(msg.dateRepresent);
	}

	if (msg.username) {
		jQuery("#username").text(msg.username);
	}

	//jQuery("#token-stats tbody").empty();

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
					// var f = false;
					// console.log(_lanes);
					// for ( var i = 0; i < _lanes.length; i++) {
					// if (_lanes[i] === el.laneRu) {
					// f = true;
					// break;
					// }
					// }
					// if (!f) {
					// console.log('111111111111111111');
					//						
					// _lanes.push(el.laneRu);
					// jQuery('#customDropdown1').append(
					// "<option value='" + el.laneRu + "'>"
					// + el.laneRu + "</option>");
					// }
				});
		setTime();
	}

}

function processMessage(msg) {
	if (msg.called) {
		var unit = jQuery("tr[data-unit='" + msg.unit + "']");
		unit.find("td[data-type=ticket]").text(msg.ticket);
		unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
		unit.find("td[data-type=called]").text(msg.called);

		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];
			if (el.lane == msg.lane) {
				el.called += 1;
			}
		});
		updateLaneStats();
		jQuery(_unitStats).each(function(ix) {
			var el = _unitStats[ix];
			if (el.username == msg.unit) {
				el.called = msg.called;
			}
		});
	} else if (msg.started) {
		var unit = jQuery("tr[data-unit='" + msg.unit + "']");
		unit.find("td[data-type=ticket]").text(msg.ticket);
		unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
		unit.find("td[data-type=started]").text(msg.started);

		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];
			if (el.lane == msg.lane) {
				el.started += 1;
			}
		});
		updateLaneStats();

		jQuery(_unitStats).each(function(ix) {
			var el = _unitStats[ix];
			if (el.username == msg.unit) {
				el.started = msg.started;
			}
		});
	} else if (msg.ended) {
		var unit = jQuery("tr[data-unit='" + msg.unit + "']");
		unit.find("td[data-type]").text("");

		console.log('msg.ended');

		var startedTime;
		var calledTime;
		var unitRealName;

		jQuery(_unitStats)
				.each(
						function(ix) {
							var el = _unitStats[ix];

							if (el.username == msg.unit) {
								startedTime = el.started;
								calledTime = el.called;
								unitRealName = el.firstname + ' ' + el.lastname;

								el.ended.push({
									ticket : msg.ticket,
									tokenId : msg.tokenId,
									isTicketReserved : msg.isTicketReserved,
									lane : msg.lane,
									call : el.called,
									start : el.started,
									end : msg.ended,
									mark : 0
								});
								el.called = undefined;
								el.started = undefined;

								jQuery(el.ended)
										.each(
												function(jx) {
													var jel = el.ended[jx];

													console
															.log('jel.tokenId == msg.tokenId : '
																	+ jel.tokenId
																	+ ' == '
																	+ msg.tokenId);

													if (jel.tokenId == msg.tokenId
															&& jel.tokenId != undefined)
														jel.isTicketReserved = msg.isTicketReserved;
												});

								/*
								 * unit.find("td[data-stat='tokenAll']").text(el.tokenAll);
								 * unit.find("td[data-stat='countMarkGood']").text(el.countMarkGood);
								 * unit.find("td[data-stat='countMarkNorm']").text(el.countMarkNorm);
								 * unit.find("td[data-stat='countMarkBad']").text(el.countMarkBad);
								 * unit.find("td[data-stat='markAverage']").text(el.markAverage);
								 */

								unit.find("td[data-stat='tokenAll']").text(
										msg.tokenAll);

								if (msg.isTicketReserved) {
									var countReserved = eval(unit.find(
											"td[data-stat='countReserved']")
											.text());
									unit.find("td[data-stat='countReserved']")
											.text(++countReserved);
								}

								unit.find("td[data-stat='countMarkGood']")
										.text(msg.countMarkGood);
								unit.find("td[data-stat='countMarkNorm']")
										.text(msg.countMarkNorm);
								unit.find("td[data-stat='countMarkBad']").text(
										msg.countMarkBad);
								unit.find("td[data-stat='markAverage']").text(
										msg.markAverage);
							}
						});

		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];

			if (el.lane == msg.lane && el.lane != undefined) {
				console.log('showLane laneFound');
				console.log(el.history);
				el.history.push({
					ticket : msg.ticket,
					tokenId : msg.tokenId,
					call : calledTime,
					start : startedTime,
					end : msg.ended,
					mark : 2,
					isTicketReserved : msg.isTicketReserved,
					unitRealName : unitRealName
				});

				el.ended += 1;
				if (msg.isTicketReserved)
					el.countReserved += 1;

				/*
				 * call: "10:25:58" end: "10:25:59" isTicketReserved: false
				 * mark: 2 start: "10:25:58" ticket: "001" tokenId: 23
				 * unitRealName: "Ермек Абдуғалиев"
				 */
			}
		});

		updateLaneStats();
	} else if (msg.missed) {
		var unit = jQuery("tr[data-unit='" + msg.unit + "']");
		unit.find("td[data-type]").text("");

		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];
			if (el.lane == msg.lane) {
				el.missed += 1;
			}
		});
		updateLaneStats();
		jQuery(_unitStats).each(function(ix) {
			var el = _unitStats[ix];
			if (el.username == msg.unit) {
				el.missed.push({
					ticket : msg.ticket,
					lane : msg.lane,
					call : el.called,
					start : el.started,
					end : msg.missed
				});
				el.called = undefined;
				el.started = undefined;
			}
		});
	} else if (msg.transferred) {
		var unit = jQuery("tr[data-unit='" + msg.unit + "']");
		unit.find("td[data-type]").text("");

		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];
			if (el.lane == msg.lane) {
				el.transferred += 1;
			}
			if (el.lane == msg.nextLane) {
				el.created += 1;
			}
		});
		updateLaneStats();

		jQuery(_unitStats).each(function(ix) {
			var el = _unitStats[ix];
			if (el.username == msg.unit) {
				el.transferred.push({
					ticket : msg.ticket,
					lane : msg.lane,
					targetLane : msg.nextLane,
					call : el.called,
					start : el.started,
					end : msg.transferred
				});
				el.called = undefined;
				el.started = undefined;
			}
		});
	} else if (msg.newticket) {
		jQuery(_laneStats).each(function(ix) {
			var el = _laneStats[ix];
			if (el.lane == msg.newticket.lane) {
				el.created += 1;
			}
		});
		updateLaneStats();
	} else if (msg.smile) {
		console.log('Yahoo! MARK');

		var unit = jQuery("tr[data-unit='" + msg.unit + "']");

		jQuery(_unitStats).each(
				function(ix) {
					var el = _unitStats[ix];
					if (el.username == msg.unit) {
						console.log('msg.unit is found');

						unit.find("td[data-stat='tokenAll']")
								.text(msg.tokenAll);
						console.log(msg.countMarkGood);
						unit.find("td[data-stat='countMarkGood']").text(
								msg.countMarkGood);
						unit.find("td[data-stat='countMarkNorm']").text(
								msg.countMarkNorm);
						unit.find("td[data-stat='countMarkBad']").text(
								msg.countMarkBad);
						unit.find("td[data-stat='markAverage']").text(
								msg.markAverage);

						jQuery(el.ended).each(
								function(jx) {
									var jel = el.ended[jx];

									console.log('jel.tokenId == msg.tokenId : '
											+ jel.tokenId + ' == '
											+ msg.tokenId);

									if (jel.tokenId == msg.tokenId
											&& jel.tokenId != undefined)
										jel.mark = msg.mark;
								});

						el.tokenAll = msg.tokenAll;
						el.countMarkGood = msg.countMarkGood;
						el.countMarkNorm = msg.countMarkNorm;
						el.countMarkBad = msg.countMarkBad;
						el.markAverage = msg.markAverage;
					}
				});

		jQuery(_laneStats).each(
				function(ix) {
					var el = _laneStats[ix];

					if (el.lane == msg.lane && el.lane != undefined) {
						jQuery(el.history).each(
								function(jx) {
									var jel = el.history[jx];

									console.log('jel.tokenId == msg.tokenId : '
											+ jel.tokenId + ' == '
											+ msg.tokenId);

									if (jel.tokenId == msg.tokenId
											&& jel.tokenId != undefined)
										jel.mark = msg.mark;
								});
					}
				});
	}
	console.log(msg);
}

function getUnit(login) {
	var result = {};
	jQuery(_unitStats).each(function(ix) {
		if (_unitStats[ix].username == login) {
			result = _unitStats[ix];
		}
	});
	return result;
}

function updateLaneStats() {
	jQuery("#lane-mon tbody tr").remove();
	jQuery(_laneStats).each(
			function(ix) {
				var el = _laneStats[ix];
				_lanes[el.lane] = el.laneText;

				jQuery("#lane-mon tbody").append(
						"<tr data-lane='" + el.lane
								+ "'><td><a href='javascript:showLane(\""
								+ el.lane + "\")' >" + el.laneText
								+ "</a></td><td>" + el.created + "</td><td>"
								+ el.countReserved + "</td><td>" + el.ended
								+ "</td><td>" + el.missed + "</td><td>"
								+ el.transferred + "</td></tr>");
			});

	jQuery("#lane-stats tbody tr").remove();
	jQuery(_laneStats).each(
			function(ix) {
				var el = _laneStats[ix];
				_lanes[el.lane] = el.laneText;

				jQuery("#lane-stats tbody").append(
						"<tr data-lane='" + el.lane
								+ "'><td><a href='javascript:showLane(\""
								+ el.lane + "\")' >" + el.laneText
								+ "</a></td><td>" + el.created + "</td><td>"
								+ el.countReserved + "</td><td>" + el.ended
								+ "</td><td>" + el.missed + "</td><td>"
								+ el.transferred + "</td></tr>");
			});
}

function showLane(lanename) {
	// console.log(_laneStats);
	jQuery(_laneStats)
			.each(
					function(ix) {
						var el = _laneStats[ix];

						if (lanename == el.lane && el.lane != undefined) {
							jQuery('#laneModal legend div span#laneName').text(
									kk_ru.line + ': ' + el.laneText);
							
							$laneModal = jQuery("#laneModal tbody");
							$laneModal.empty();

							console.log('showLane laneFound');
							console.log(el.history);
							
							for ( var prop in el.history) {
								var jel = el.history[prop];

								console.log(jel);

								$laneModal.append("<tr>");
								$laneModal.append(
										"<td>" + jel.unitRealName + "</td>");
								
								var isReservedText = (jel.isTicketReserved ? ' '
										+ kk_ru.reserve
										: '')
								$laneModal.append(
										"<td>" + jel.ticket + isReservedText
												+ "</td>");
								$laneModal.append(
										"<td>" + (jel.start!='null' ? jel.start : '') + "</td><td>"
												+ (jel.end!='null' ? jel.end : '') + "</td>");

								var markText = '';

								switch (jel.mark) {
								case 0:
									markText = '';
									break;
								case 5:
									markText = kk_ru.excellent;
									break;
								case 4:
									markText = kk_ru.good;
									break;
								case 3:
									markText = kk_ru.norm;
									break;
								case 2:
									markText = kk_ru.bad;
									break;
								case 1:
									markText = kk_ru.veryBad;
									break;
								}
								jQuery("#laneModal tbody").append(
										"<td>" + markText + "</td>");

								jQuery("#laneModal tbody").append("</tr>");
							}
							$('#laneModal').foundation('reveal', 'open');
						}
					});
	return false;
}

function showUnit(username) {
	jQuery(_unitStats)
			.each(
					function(ix) {
						var el = _unitStats[ix];
						if (username.indexOf(el.username) != -1) {
							jQuery('#unitModal legend div span#unitRealname')
									.text(
											'Оператор: ' + el.firstname + ' '
													+ el.lastname);
							jQuery('#unitModal legend div span#unitUsername')
									.text("Логин: " + el.username);

							jQuery("#unitModal ul.unitLanes").empty();
							jQuery(el.lanes)
									.each(
											function(ixx) {
												jQuery(
														"#unitModal ul.unitLanes")
														.append(
																"<li><span class='label'>"
																		+ el.lanes[ixx].laneText
																		+ "</span></li>");
											});

							jQuery("#unitModal tbody").empty();
							jQuery(el.ended)
									.each(
											function(jx) {
												var jel = el.ended[jx];

												var laneText = jel.laneText;

												if (laneText == undefined) {
													for ( var prop in _lanes)
														if (prop == jel.lane) {
															laneText = _lanes[prop];
															break;
														}
												}

												jQuery("#unitModal tbody")
														.append("<tr>");
												jQuery("#unitModal tbody")
														.append(
																"<td>"
																		+ laneText
																		+ "</td>");

												var isReservedText = (jel.isTicketReserved ? ' Бронь'
														: '');
												console
														.log('jel.isTicketReserved = '
																+ jel.isTicketReserved
																+ ' isReservedText = '
																+ isReservedText);
												jQuery("#unitModal tbody")
														.append(
																"<td>"
																		+ jel.ticket
																		+ isReservedText
																		+ "</td>");

												jQuery("#unitModal tbody")
														.append(
																"<td>"
																		+ jel.start
																		+ "</td><td>"
																		+ jel.end
																		+ "</td>");

												var markText = '';

												switch (jel.mark) {
												case 0:
													markText = '';
													break;
												case 5:
													markText = kk_ru.excellent;
													break;
												case 4:
													markText = kk_ru.good;
													break;
												case 3:
													markText = kk_ru.norm;
													break;
												case 2:
													markText = kk_ru.bad;
													break;
												case 1:
													markText = kk_ru.veryBad;
													break;
												}
												jQuery("#unitModal tbody")
														.append(
																"<td>"
																		+ markText
																		+ "</td>");

												jQuery("#unitModal tbody")
														.append("</tr>");
											});
							$('#unitModal').foundation('reveal', 'open');
						}
					});
	return false;
}

function goToAdmin() {
	console.log('gotolocaladmin');
	jQuery("#body").load("localAdmin.html", function(html) {
		if (typeof _init == "function") {
			console.log(_lang);
			_init('ru');
		}
		jQuery("#body").show();
	});
}

function _onStatus(status) {
	if (status == "CONNECTED" || status == "ATTACHED") {
		onConnect();
	}
	if (status == "DISCONNECTED") {
		console.log("Disconnected");
	}
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
	// jQuery('#dropdown_menu').hide();
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

	jQuery("#help").empty();
	jQuery("#help").append(kk_ru.help);
	jQuery(".settings").empty();
	jQuery(".settings").append(kk_ru.settings);
	jQuery("#logoutLink").empty();
	jQuery("#logoutLink").append(kk_ru.quit);
	jQuery("#go_to_statistics").empty();
	jQuery("#go_to_statistics").append(kk_ru.go_to_statistics);
	jQuery("#go_to_tickets").text(kk_ru.go_to_tickekk_ru.go_to_statisticsts);
	jQuery(".line").empty();
	jQuery(".line").append(kk_ru.line);
	jQuery(".all").text(kk_ru.all);
	jQuery(".reserve").text(kk_ru.reserve);
	jQuery(".serviced").text(kk_ru.serviced);
	jQuery(".not_came").text(kk_ru.notcame);
	jQuery(".routed").text(kk_ru.routed);
	jQuery(".operator").empty();
	jQuery(".operator").append(kk_ru.operator);
	jQuery("#window").empty();
	jQuery("#window").append(kk_ru.window);
	jQuery("#call_time").empty();
	jQuery("#call_time").append(kk_ru.call_time);
	jQuery("#start_time").empty();
	jQuery("#start_time").append(kk_ru.start_time);
	jQuery("#ticket_time").empty();
	jQuery("#ticket_time").append(kk_ru.ticket_number);
	jQuery(".excell").text(kk_ru.excellent);
	jQuery("#good").text(kk_ru.good);
	jQuery(".bad").text(kk_ru.bad);
	jQuery("#average").empty();
	jQuery("#average").append(kk_ru.average);
	jQuery(".history").empty();
	jQuery(".history").append(kk_ru.hi);
	jQuery(".ticket").empty();
	jQuery(".ticket").append(kk_ru.ticket);
	jQuery(".start").empty();
	jQuery(".start").append(kk_ru.start);
	jQuery(".end").empty();
	jQuery(".end").append(kk_ru.end);
	jQuery(".mark").empty();
	jQuery(".mark").append(kk_ru.mark);
	jQuery("#monitoring").text(kk_ru.monitoring);

	console.log("LANGUAGE IS: " + _lang);
	$('#message').foundation('reveal', 'close');
	XmppClient.send("queue", JSON.stringify({
		action : "changeLang",
		user : sessionStorage.jid,
		language : _lang
	}));
	// locale(_lang);
}

jQuery("#logoutLink").click(function() {
	console.log("logoutLink was clicked");
	window.location = "/";
	sessionStorage.clear();
	XmppClient.connection.disconnect();
	// jQuery("#loginPanel").show();
	// jQuery("#userPanel").hide();
});

function saveText() {
	var text = kk_ru.line + ';' + kk_ru.all + ';' + kk_ru.reserve + ';'
			+ kk_ru.serviced + ';' + kk_ru.notcame + ';' + kk_ru.routed + "\n";
	jQuery(_laneStats).each(
			function(ix) {
				var el = _laneStats[ix];
				text += el.laneText + ";" + el.created + ";" + el.countReserved
						+ ";" + el.ended + ";" + el.missed + ";"
						+ el.transferred + "\n";
			});
	var blob = new Blob([ text ], {
		type : "text/plain;charset=Unicode",
	});
	saveAs(blob, "monitor_lane.csv");
	return false;
}

function saveUnitText() {
	var textUnit = kk_ru.operator + ';' + kk_ru.window + ';' + kk_ru.line + ';'
			+ kk_ru.call_time + ';' + kk_ru.start_time + ';'
			+ kk_ru.ticket_number + ';' + kk_ru.all + ';' + kk_ru.reserve + "\n";
	jQuery(_unitStats).each(
			function(ix) {
				var el = _unitStats[ix];
				textUnit += el.firstname + " " + el.lastname + ";" + el.window
						+ ";"
						+ (_lanes[el.lane] != undefined ? _lanes[el.lane] : "")
						+ ";" + (el.called != undefined ? el.called : "") + ";"
						+ (el.started != undefined ? el.started : "") + ";"
						+ (el.ticketCode != undefined ? el.ticketCode : "")
						+ ";" + el.tokenAll + ";" + el.countReserved + "\n"
			});
	var blob = new Blob([ textUnit ], {
		type : "text/plain;charset=Unicode",
	});
	saveAs(blob, "monitor_unit.csv");
	return false;
}

// ticket.js
var totalSeconds = 0;
var _msg = null;
var _lang = null;
var _id = null;
var timeArray = new Array();
var selectedLane = '';

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

function saveTicketText() {
	var textTicket = kk_ru.line + ';' + kk_ru.number + ';' + kk_ru.window
			+ ';' + kk_ru.create_time + ';' + kk_ru.call_time + ';' + kk_ru.start_time + ';'
			+ kk_ru.priority + ';' + kk_ru.waiting_time  + "\n";
	jQuery(_msg.tokens).each(
			function(ix) {
				var el = _msg.tokens[ix];
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

function saveMarkText() {
	var textUnit = kk_ru.operator + ';'
			+ kk_ru.excellent + ';' + kk_ru.good + ';'
			+ kk_ru.norm + ';' + kk_ru.bad + ';' + kk_ru.veryBad + ';'
			+ kk_ru.average + "\n";
	jQuery(_unitStats).each(
			function(ix) {
				var el = _unitStats[ix];
				textUnit += el.firstname + " " + el.lastname + ";"
						+ el.countMarkBest + ";" + el.countMarkGood + ";"
						+ el.countMarkNorm + ";" + el.countMarkBad + ";"
						+ +el.countMarkVeryBad + ";" + el.markAverage + "\n"
			});
	var blob = new Blob([ textUnit ], {
		type : "text/plain;charset=Unicode",
	});
	saveAs(blob, "mark_unit.csv");
	return false;
}
// statistcs
var firstTime = 0;
var nowTemp = new Date();
var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(),
		nowTemp.getDate(), 0, 0, 0, 0);

function showRepresentDate() {
	console.log('showRepresentdate');
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp
			.getDate(), 0, 0, 0, 0);
	var checkin = $('#datepickerFrom').fdatepicker({
		format : 'dd.mm.yyyy'
	}).on('changeDate', function(ev) {
		if (ev.date.valueOf() > checkout.date.valueOf()) {
			var newDate = new Date(ev.date)
			newDate.setDate(newDate.getDate() + 1);
			checkout.setValue(newDate);
		}
		checkin.hide();
		$('#datepickerTo')[0].focus();
	}).data('datepicker');
	var checkout = $('#datepickerTo').fdatepicker({
		format : 'dd.mm.yyyy',
		onRender : function(date) {
			return date.valueOf() <= checkin.date.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev) {
		checkout.hide();
	}).data('datepicker');
	if (firstTime == 0) {
		$("body")
				.append(
						jQuery("<script src='javascripts/foundation/foundation.datepicker.js'></script"));
	}

	$('#representDateModal').foundation('reveal', 'open');
}
// Value parameter - required. All other parameters are optional.

function isDate(value, sepVal, dayIdx, monthIdx, yearIdx) {
	try {
		value = value.replace(/-/g, "/").replace(/\./g, "/");
		sepVal = (sepVal === undefined ? "/" : sepVal.replace(/-/g, "/")
				.replace(/\./g, "/"));

		var SplitValue = value.split(sepVal);
		if (SplitValue.length != 3) {
			return false;
		}

		// Auto detection of indexes
		if (dayIdx === undefined || monthIdx === undefined
				|| yearIdx === undefined) {
			if (SplitValue[0] > 31) {
				yearIdx = 0;
				monthIdx = 1;
				dayIdx = 2;
			} else {
				yearIdx = 2;
				monthIdx = 1;
				dayIdx = 0;
			}
		}

		// Change the below values to determine which format of date you wish to
		// check. It is set to dd/mm/yyyy by default.
		var DayIndex = dayIdx !== undefined ? dayIdx : 0;
		var MonthIndex = monthIdx !== undefined ? monthIdx : 1;
		var YearIndex = yearIdx !== undefined ? yearIdx : 2;
		console.log(SplitValue);

		console.log(DayIndex);
		console.log(MonthIndex);
		console.log(YearIndex);

		var OK = true;
		if (!(SplitValue[DayIndex].length == 1 || SplitValue[DayIndex].length == 2)) {
			OK = false;
		}
		console.log(OK);
		if (OK
				&& !(SplitValue[MonthIndex].length == 1 || SplitValue[MonthIndex].length == 2)) {
			OK = false;
		}
		console.log(OK);
		if (OK && SplitValue[YearIndex].length != 4) {
			OK = false;
		}
		console.log(OK);
		if (OK) {
			var Day = parseInt(SplitValue[DayIndex], 10);
			var Month = parseInt(SplitValue[MonthIndex], 10);
			var Year = parseInt(SplitValue[YearIndex], 10);
			var MonthDays = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

			if (OK = (Month <= 12 && Month > 0)) {

				var LeapYear = (Year & 3) == 0
						&& ((Year % 25) != 0 || (Year & 15) == 0);
				MonthDays[1] = (LeapYear ? 29 : 28);

				OK = Day > 0 && Day <= MonthDays[Month - 1];
			}
		}
		console.log(OK);
		return OK;
	} catch (e) {
		return false;
	}
}

function goToDate() {
	var dateFrom = jQuery("#datepickerFrom").val();
	var dateTo = jQuery("#datepickerTo").val();
	console.log(dateFrom);
	if (!isDate(dateFrom, '.')) {
		jQuery("label#datepickerFrom_error").html('Некорректная дата.');
		jQuery("label#datepickerFrom_error").show();
		jQuery("input#datepickerFrom").focus();
		return;
	}

	if (!isDate(dateTo, '.')) {
		jQuery("label#datepickerTo_error").html('Некорректная дата.');
		jQuery("label#datepickerTo_error").show();
		jQuery("input#datepickerTo").focus();
		return;
	}

	jQuery('.error').hide();
	$('#representDateModal').foundation('reveal', 'close');

	var msg = {
		action : "monitor_representDate",
		dateFrom : dateFrom,
		dateTo : dateTo
	};
	XmppClient.send("queue", JSON.stringify(msg));
}

function saveLaneStat() {
    var textUnit = jQuery("#representDateLane").text() + "\n" + kk_ru.line + ';'
    + kk_ru.all + ';' + kk_ru.reserve + ';' + kk_ru.serviced + ';' 
    + kk_ru.notcame + ';' + kk_ru.routed + "\n";

    jQuery(_laneStats).each(function (ix) {
        var el = _laneStats[ix];
        textUnit += el.laneText + ";" +
            el.created + ";" +
            el.countReserved + ";" +
            el.ended + ";" +
            el.missed + ";" +
            el.transferred + "\n";
    });
    var blob = new Blob([textUnit], {
        type: "text/plain;charset=Unicode",
    });
    saveAs(blob, "statistics_lane.csv");
    return false;
}

function saveUnitStat() {

    var textUnit = jQuery("#representDateUnit").text() + "\n" + kk_ru.operator + ';' 
    + kk_ru.window + ';' + kk_ru.all + ';' + kk_ru.reserve + ';' +
    kk_ru.excellent + ';' + kk_ru.good + ';' + kk_ru.norm + ';' + kk_ru.bad 
    + ';' + kk_ru.veryBad + ';' + kk_ru.average + "\n";

    jQuery(_unitStats).each(function (ix) {
        var el = _unitStats[ix];
        textUnit += el.firstname + " " + el.lastname + ";" +
            el.window + ";" +
            el.tokenAll + ";" +
            el.countReserved + ";" +
            el.countMarkBest + ";" +
            el.countMarkGood + ";" +
            el.countMarkNorm + ";" +
            el.countMarkBad + ";" +
            el.countMarkVeryBad + ";" +
            el.markAverage + "\n";
    });

    var blob = new Blob([textUnit], {
        type: "text/plain;charset=Unicode",
    });
    saveAs(blob, "statistics_unit.csv");
    return false;
}
