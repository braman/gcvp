var _kk = {
	help: "Көмек",
	settings: "Қондырғылар",
	quit: "Шығу",
	monitoring: "Бақылау",
	go_to_statistics: "Санаққа өту",
	go_to_tickets: "Текущие билеты",
	line: "Қызмет тізімі",
	all: "Барлығы",
	reserve: "Бронь",
	serviced: "Қызмет корсетілген",
	notcame: "Келмеген клиенттер",
	routed: "Қайта бағытталған",
	operator: "Оператор",
	window: "Терезе",
	call_time: "Шақырылған уақыты",
	start_time: "Басталған уақыты",
	ticket_number: "Билет нөмірі",
	excellent: "Өте жақсы",
	good: "Орташа",
	bad: "Нашар",
	average: "Орташа бағасы",
	history: "Тарихы",
	ticket: "Билет",
	start: "Басы",
	end: "Соңы",
	mark: "Бағасы",
	choose_lang: "Тіл таңдаңыз"
};
var _ru = {
		help: "Помощь",
		settings: "Настройки",
		quit: "Выход",
		monitoring: "Мониторинг",
		go_to_statistics: "Перейти на статистику",
		go_to_tickets: "Текущие билеты",
		line: "Услуги",
		all: "Всего",
		reserve: "Бронь",
		serviced: "Обслужено",
		notcame: "Клиент не пришел",
		routed: "Перенаправленные",
		operator: "Оператор",
		window: "Окно",
		call_time: "Время вызова",
		start_time: "Время начала",
		ticket_number: "Номер билета",
		excellent: "Отлично",
		good: "Удовлетворительно",
		bad: "Плохо",
		average: "Средняя оценка",
		history: "История",
		ticket: "Билет",
		start: "Начало",
		end: "Конец",
		mark: "Оценка",
		choose_lang: "Выберите язык"
};

var _unitStats = {};
// var _unitStatistics = {};
var _laneStats = {};
var _lanes = {};
var _msgBuffer = new Array();
var _lang = null;
var kk_ru = _ru;



function _init(data){
location.href = "#!monitor";
jQuery("head").append(jQuery("<link rel='stylesheet' href='stylesheets/app.min.css' />"));
document.title = 'CQ - Monitor';

if(data.lang){
	_lang = data.lang;

	var translated = "";
	console.log("Lang: " + _lang);
	
	if(_lang=="kk"){
		kk_ru = _kk;
	}
	else if(_lang=="ru"){
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
	jQuery("#routed").append(kk_ru.routed);
	jQuery(".operator").append(kk_ru.operator);
	jQuery("#window").append(kk_ru.window);
	jQuery("#call_time").append(kk_ru.call_time);
	jQuery("#start_time").append(kk_ru.start_time);
	jQuery("#ticket_number").text(kk_ru.ticket_number);
	jQuery("#excell").append(kk_ru.excellent);
	jQuery("#good").append(kk_ru.good);
	jQuery("#bad").append(kk_ru.bad);
	jQuery("#average").append(kk_ru.average);
	jQuery(".history").append(kk_ru.hi);
	jQuery(".ticket").append(kk_ru.ticket);
	jQuery(".start").append(kk_ru.start);
	jQuery(".end").append(kk_ru.end);
	jQuery("#monitoring").text(kk_ru.monitoring);
	jQuery(".mark").append(kk_ru.mark);
}

jQuery('.error').hide();

onConnect();
}

function _initFromStatistics(lang){
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
		else if(_lang=="ru"){
			kk_ru = _ru;
		}
		else if(_lang=="en"){
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

function onConnect(){
XmppClient.send("queue","init");	
}

function _onMessage(text){
console.log("_onMessage:"+text);
var msg = {};
try{
msg = JSON.parse(text);
}catch(e){
console.warn("unit got non-JSON message:"+text);
return;
}

if(msg.username){
         jQuery("#username").text(msg.username);
        }
        
if(msg.laneStats){
_laneStats = msg.laneStats;
updateLaneStats();
}
if(msg.unitStats){
_unitStats = msg.unitStats;
jQuery("#unit-stats tbody").empty();
jQuery(_unitStats).each(function(ix){
	var el = _unitStats[ix];
	
	jQuery("#unit-stats tbody").append(
	"<tr data-unit='"+el.username+"'>" +
	"<td><a href='#' onclick='showUnit(\""+el.username+"\")'>" +el.firstname + " " + el.lastname+"</a></td>"+
	"<td>" +el.window+"</td>"+
	"<td data-type='lane'>" +(_lanes[el.lane]!=undefined?_lanes[el.lane]:"")+"</td>"+
	"<td data-type='called'>"	+(el.called!=undefined?el.called:"")+"</td>"+
	"<td data-type='started'>" +(el.started!=undefined?el.started:"")+"</td>"+
	"<td data-type='ticket'>" +(el.ticketCode!=undefined?el.ticketCode:"")+"</td>" +
	"<td data-stat='tokenAll'>"+el.tokenAll+"</td>"+
	"<td data-stat='countReserved'>" + el.countReserved + "</td>"+
	"<td data-stat='countMarkGood'>" +el.countMarkGood + "</td>"+
	"<td data-stat='countMarkNorm'>" +el.countMarkNorm + "</td>"+
	"<td data-stat='countMarkBad'>" +el.countMarkBad + "</td>"+
	"<td data-stat='markAverage'>" +el.markAverage + "</td>"+
	"</tr>");
	});
}
if(_laneStats.length==undefined || _unitStats.length==undefined){
_msgBuffer.push(msg);
return;
}else{
if(_msgBuffer.length!=0){
jQuery(_msgBuffer).each(function(ix){
processMessage(_msgBuffer[ix]);
});
_msgBuffer = new Array();
}
}
processMessage(msg);
}

function processMessage(msg){
if(msg.called){
var unit = jQuery("tr[data-unit='"+msg.unit+"']");
unit.find("td[data-type=ticket]").text(msg.ticket);
unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
unit.find("td[data-type=called]").text(msg.called);

jQuery(_laneStats).each(function(ix){
var el = _laneStats[ix];
if(el.lane==msg.lane){
el.called += 1;
}
});
updateLaneStats();
jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];
if(el.username==msg.unit){
el.called = msg.called;
}
});
}else
if(msg.started){
	var unit = jQuery("tr[data-unit='"+msg.unit+"']");
	unit.find("td[data-type=ticket]").text(msg.ticket);
	unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
	unit.find("td[data-type=started]").text(msg.started);
	
	jQuery(_laneStats).each(function(ix){
		var el = _laneStats[ix];
		if(el.lane==msg.lane){
			el.started += 1;
		}
	});
	updateLaneStats();

	jQuery(_unitStats).each(function(ix){
		var el = _unitStats[ix];
		if(el.username==msg.unit){
			el.started = msg.started;
		}
	});
}else
if(msg.ended){
var unit = jQuery("tr[data-unit='"+msg.unit+"']");
unit.find("td[data-type]").text("");

  console.log('msg.ended');

var startedTime;
var calledTime;
var unitRealName;
 
  jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];

if(el.username==msg.unit){
startedTime = el.started;
calledTime = el.called;
unitRealName = el.firstname + ' ' + el.lastname;

el.ended.push({ticket:msg.ticket,tokenId:msg.tokenId,isTicketReserved:msg.isTicketReserved,lane:msg.lane,call:el.called,start:el.started,end:msg.ended,mark:2});
el.called = undefined;
el.started = undefined;


jQuery(el.ended).each(function(jx){
var jel = el.ended[jx];

console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

if (jel.tokenId==msg.tokenId && jel.tokenId != undefined)
jel.isTicketReserved = msg.isTicketReserved;
});

/* unit.find("td[data-stat='tokenAll']").text(el.tokenAll);
unit.find("td[data-stat='countMarkGood']").text(el.countMarkGood);
unit.find("td[data-stat='countMarkNorm']").text(el.countMarkNorm);
unit.find("td[data-stat='countMarkBad']").text(el.countMarkBad);
unit.find("td[data-stat='markAverage']").text(el.markAverage);
*/

unit.find("td[data-stat='tokenAll']").text(msg.tokenAll);

if (msg.isTicketReserved) {
	var countReserved = eval (unit.find("td[data-stat='countReserved']").text() );
	unit.find("td[data-stat='countReserved']").text(++countReserved);
}

unit.find("td[data-stat='countMarkGood']").text(msg.countMarkGood);
unit.find("td[data-stat='countMarkNorm']").text(msg.countMarkNorm);
unit.find("td[data-stat='countMarkBad']").text(msg.countMarkBad);
unit.find("td[data-stat='markAverage']").text(msg.markAverage);
}
});

  jQuery(_laneStats).each(function(ix){
  var el = _laneStats[ix];
 
  if (el.lane == msg.lane && el.lane != undefined) {
  console.log('showLane laneFound');
  console.log(el.history);
  el.history.push({ticket:msg.ticket,tokenId:msg.tokenId,call:calledTime,start:startedTime,end:msg.ended,mark:2,isTicketReserved:msg.isTicketReserved,unitRealName:unitRealName});

el.ended += 1;
if (msg.isTicketReserved) el.countReserved += 1;

/* call: "10:25:58"
end: "10:25:59"
isTicketReserved: false
mark: 2
start: "10:25:58"
ticket: "001"
tokenId: 23
unitRealName: "Ермек Абдуғалиев"
*/	
  }
  });
 
  updateLaneStats();
}else
if(msg.missed){
var unit = jQuery("tr[data-unit='"+msg.unit+"']");
unit.find("td[data-type]").text("");

jQuery(_laneStats).each(function(ix){
var el = _laneStats[ix];
if(el.lane==msg.lane){
el.missed += 1;
}
});
updateLaneStats();
jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];
if(el.username==msg.unit){
el.missed.push({ticket:msg.ticket,lane:msg.lane,call:el.called,start:el.started,end:msg.missed});
el.called = undefined;
el.started = undefined;
}
});
}else
if(msg.transferred){
var unit = jQuery("tr[data-unit='"+msg.unit+"']");
unit.find("td[data-type]").text("");

jQuery(_laneStats).each(function(ix){
var el = _laneStats[ix];
if(el.lane==msg.lane){
el.transferred += 1;
}
if(el.lane==msg.nextLane){
el.created += 1;
}
});
updateLaneStats();

jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];
if(el.username==msg.unit){
el.transferred.push({ticket:msg.ticket,lane:msg.lane,targetLane:msg.nextLane,call:el.called,start:el.started,end:msg.transferred});
el.called = undefined;
el.started = undefined;
}
});
}else
if(msg.newticket){
jQuery(_laneStats).each(function(ix){
var el = _laneStats[ix];
if(el.lane==msg.newticket.lane){
el.created += 1;
}
});
updateLaneStats();
}else
if (msg.smile) {
console.log('Yahoo! MARK');

var unit = jQuery("tr[data-unit='"+msg.unit+"']");

jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];
if(el.username==msg.unit){
console.log('msg.unit is found');

unit.find("td[data-stat='tokenAll']").text(msg.tokenAll);
console.log(msg.countMarkGood);
unit.find("td[data-stat='countMarkGood']").text(msg.countMarkGood);
unit.find("td[data-stat='countMarkNorm']").text(msg.countMarkNorm);
unit.find("td[data-stat='countMarkBad']").text(msg.countMarkBad);
unit.find("td[data-stat='markAverage']").text(msg.markAverage);

jQuery(el.ended).each(function(jx){
var jel = el.ended[jx];

console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

if (jel.tokenId==msg.tokenId && jel.tokenId != undefined)
jel.mark = msg.mark;
});

el.tokenAll = msg.tokenAll;
el.countMarkGood = msg.countMarkGood;
el.countMarkNorm = msg.countMarkNorm;
el.countMarkBad = msg.countMarkBad;
el.markAverage = msg.markAverage;
}
});

  jQuery(_laneStats).each(function(ix){
  var el = _laneStats[ix];

  if (el.lane == msg.lane && el.lane != undefined) {
  jQuery(el.history).each(function(jx){
var jel = el.history[jx];

console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

if (jel.tokenId==msg.tokenId && jel.tokenId != undefined)
jel.mark = msg.mark;
});
  }
  });
}
console.log(msg);
}

function getUnit(login){
var result = {};
jQuery(_unitStats).each(function(ix){
if(_unitStats[ix].username==login){
result = _unitStats[ix];
}
});
return result;
}

function updateLaneStats(){
jQuery("#lane-stats tbody tr").remove();
jQuery(_laneStats).each(function(ix){
	var el = _laneStats[ix];
	_lanes[el.lane] = el.laneText;
	
	jQuery("#lane-stats tbody").append("<tr data-lane='"+el.lane+"'><td><a href='javascript:showLane(\"" + el.lane + "\")' >"+el.laneText+"</a></td><td>"
		+el.created+"</td><td>"
		+el.countReserved+"</td><td>"
		+el.ended+"</td><td>"
		+el.missed+"</td><td>"	+el.transferred+"</td></tr>");
});
}

function showLane(lanename){
// console.log(_laneStats);
jQuery(_laneStats).each(function(ix){
var el = _laneStats[ix];

if (lanename == el.lane && el.lane != undefined) {
jQuery('#laneModal legend div span#laneName').text(kk_ru.line + ': ' + el.laneText);

jQuery("#laneModal tbody").empty();

console.log('showLane laneFound');
console.log(el.history);

for (var prop in el.history) {
var jel = el.history[prop];

console.log(jel);

jQuery("#laneModal tbody").append("<tr>");
                    jQuery("#laneModal tbody").append("<td>"+jel.unitRealName+"</td>");

                    var isReservedText = (jel.isTicketReserved ? ' '+kk_ru.reserve : '')
                    jQuery("#laneModal tbody").append("<td>"+jel.ticket + isReservedText + "</td>");
                    jQuery("#laneModal tbody").append("<td>"+jel.start+"</td><td>"+jel.end+"</td>");
                    
                    var markText = '';

                    switch (jel.mark) {
                     case 0: markText = ''; break;
                     case 5: markText = kk_ru.excellent; break;
                     case 3: markText = kk_ru.good; break;
                     case 1: markText = kk_ru.bad; break;
                    }
                    jQuery("#laneModal tbody").append("<td>" + markText + "</td>");

jQuery("#laneModal tbody").append("</tr>");	
}
$('#laneModal').foundation('reveal','open');
            }
});
return false;
}


function showUnit(username){
jQuery(_unitStats).each(function(ix){
var el = _unitStats[ix];
if(username.indexOf(el.username)!=-1){
jQuery('#unitModal legend div span#unitRealname').text('Оператор: ' + el.firstname + ' ' + el.lastname);
jQuery('#unitModal legend div span#unitUsername').text("Логин: " + el.username);

jQuery("#unitModal ul.unitLanes").empty();

jQuery(el.lanes).each(function(ixx){
	jQuery("#unitModal ul.unitLanes").append("<li><span class='label'>"+el.lanes[ixx].laneText+"</span></li>");
});

jQuery("#unitModal tbody").empty();
jQuery(el.ended).each(function(jx){
var jel = el.ended[jx];

var laneText = jel.laneText;

                 if (laneText == undefined) {
for (var prop in _lanes)
if (prop == jel.lane) {
laneText = _lanes[prop];
break;
}
}

                    jQuery("#unitModal tbody").append("<tr>");
                    jQuery("#unitModal tbody").append("<td>"+laneText+"</td>");
                    
                    var isReservedText = (jel.isTicketReserved ? ' Бронь' : '');
                    console.log('jel.isTicketReserved = ' + jel.isTicketReserved + ' isReservedText = ' + isReservedText);
                    jQuery("#unitModal tbody").append("<td>"+jel.ticket + isReservedText + "</td>");
                    
                    jQuery("#unitModal tbody").append("<td>"+jel.start+"</td><td>"+jel.end+"</td>");
                    
                    var markText = '';

                    switch (jel.mark) {
                     case 0: markText = ''; break;
                     case 5: markText = kk_ru.excellent; break;
                     case 3: markText = kk_ru.good; break;
                     case 1: markText = kk_ru.bad; break;
                    }
                    jQuery("#unitModal tbody").append("<td>" + markText + "</td>");

jQuery("#unitModal tbody").append("</tr>");	
});
$('#unitModal').foundation('reveal','open');	
}
});
return false;
}

function goToStatictics() {
	console.log('gotostatistics');
	jQuery("#body").load("/statistics.html",function(html){
		if(typeof _init=="function"){
			_init();
		}
		jQuery("#body").show();
	});
}

function goToTickets() {
	console.log('gototickets');
	jQuery("#body").load("/tickets.html",function(html){
		if(typeof _init=="function"){
			_init();
		}
		jQuery("#body").show();
	});
}

function _onStatus(status){
	if(status=="CONNECTED" || status=="ATTACHED"){
		onConnect();
	}
	if(status=="DISCONNECTED"){
		console.log("Disconnected");
	}
}

function getSettings(){
	jQuery('#messageBody').html(
			'<p>'+kk_ru.choose_lang+'</p>' +
			'<label for="ru_lang"><input type="radio" id="ru_lang" name="lang" value="ru" />Русский</label>' +
			'<label for="kk_lang"><input type="radio" id="kk_lang" name="lang" value="kk" />Казахский</label>');
	$('#message').foundation('reveal','open');
 //   jQuery('#dropdown_menu').hide();
}

function change_lang(){
	var inputs = document.getElementsByName("lang");
    for (var i = 0; i < inputs.length; i++) {
      if (inputs[i].checked) {
        _lang = inputs[i].value;
      }
    }
    
    if(_lang=="kk"){
		kk_ru = _kk;
	}
	else if(_lang=="ru"){
		kk_ru = _ru;
	}
	else if(_lang=="en"){
		kk_ru = _ru;
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
	jQuery(".all").empty();
	jQuery(".all").append(kk_ru.all);
	jQuery(".reserve").empty();
	jQuery(".reserve").append(kk_ru.reserve);
	jQuery("#serviced").empty();
	jQuery("#serviced").append(kk_ru.serviced);
	jQuery("#not_came").empty();
	jQuery("#not_came").append(kk_ru.notcame);
	jQuery("#routed").empty();
	jQuery("#routed").append(kk_ru.routed);
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
	jQuery("#excell").empty();
	jQuery("#excell").append(kk_ru.excellent);
	jQuery("#good").empty();
	jQuery("#good").append(kk_ru.good);
	jQuery("#bad").empty();
	jQuery("#bad").append(kk_ru.bad);
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
    $('#message').foundation('reveal','close');
	XmppClient.send("queue", JSON.stringify({action:"changeLang", user:sessionStorage.jid, language:_lang}));
	//locale(_lang);
}

jQuery("#logoutLink").click(function(){
	console.log("logoutLink was clicked");
	window.location = "/";
    sessionStorage.clear();
    XmppClient.connection.disconnect();
 //   jQuery("#loginPanel").show();
 //   jQuery("#userPanel").hide();
});

function saveText(){
	var text = kk_ru.line+';'+kk_ru.all+';'+kk_ru.reserve+';'+kk_ru.serviced+';'+kk_ru.notcame+';'+kk_ru.routed+"\n";
	jQuery(_laneStats).each(function(ix){
		var el = _laneStats[ix];
		text+=el.laneText+";"+el.created+";"+el.countReserved+";"+el.ended+";"+el.missed+";"+el.transferred+"\n";
	});
	var blob = new Blob([text], {
	    type: "text/plain;charset=Unicode",
	});
	saveAs(blob, "monitor_lane.csv");
	return false;
}

function saveUnitText(){
	var textUnit = kk_ru.operator+';'+kk_ru.window+';'+kk_ru.line+';'+kk_ru.call_time+';'+kk_ru.start_time+';'+kk_ru.ticket_number+';'+kk_ru.all+';'+kk_ru.reserve+';'+kk_ru.excellent+';'+kk_ru.good+';'+kk_ru.bad+';'+kk_ru.average+"\n";
	jQuery(_unitStats).each(function(ix){
		var el = _unitStats[ix];
		textUnit+=el.firstname + " " + el.lastname+";"+
		el.window+";"+
		(_lanes[el.lane]!=undefined?_lanes[el.lane]:"")+";"+
		(el.called!=undefined?el.called:"")+";"+
		(el.started!=undefined?el.started:"")+";"+
		(el.ticketCode!=undefined?el.ticketCode:"")+";"+
		el.tokenAll+";"+
		el.countReserved+";"+
		el.countMarkGood+";"+
		el.countMarkNorm+";"+
		+el.countMarkBad+";"+
		el.markAverage+"\n" 
	});
	var blob = new Blob([textUnit], {
	    type: "text/plain;charset=Unicode",
	});
	saveAs(blob, "monitor_unit.csv");
	return false;
}
