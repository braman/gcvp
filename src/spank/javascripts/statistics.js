var _msg = null;
var _lang = null;
var _kk = {
    help: "Көмек",
    settings: "Қондырғылар",
    quit: "Шығу",
    statistics: "Санақ",
    monitoring: "Бақылау",
    line: "Қызмет тізімі",
    all: "Барлығы",
    reserve: "Бронь",
    serviced: "Қызмет корсетілген",
    not_came: "Келмеген клиенттер",
    postponed: "Қайта бағытталған",
    operator: "Оператор",
    window: "Nthtpt",
    call_time: "Шақырылған уақыты",
    start_time: "Басталған уақыты",
    ticket_number: "Билет нөмірі",
    excellent: "Өте жақсы",
    good: "Орташа",
    bad: "Нашар",
    average: "Орташа бағасы",
    choose_lang: "Тіл таңдаңыз"
};

var _ru = {
    help: "Помощь",
    settings: "Настройки",
    quit: "Выход",
    statistics: "Статистика",
    monitoring: "Мониторинг",
    line: "Линия",
    all: "Всего",
    reserve: "Бронь",
    serviced: "Обслужено",
    not_came: "Клиент не пришел",
    postponed: "Перенаправленные",
    operator: "Оператор",
    window: "Окно",
    call_time: "Время вызова",
    start_time: "Время начала",
    ticket_number: "Номер билета",
    excellent: "Отлично",
    good: "Удовлетворительно",
    bad: "Плохо",
    average: "Средняя оценка",
    choose_lang: "Выберите язык"
};

var _en = {
    help: "Help",
    settings: "Settings",
    quit: "Quit",
    statistics: "Statistics",
    monitoring: "Monitoring",
    line: "Line",
    all: "All",
    reserve: "Reserve",
    serviced: "Serviced",
    not_came: "Client hasn't come",
    postponed: "Postponed",
    operator: "Operator",
    window: "Window",
    call_time: "Call time",
    start_time: "Start time",
    ticket_number: "Number of ticket",
    excellent: "Excellent",
    good: "Good",
    bad: "Bad",
    average: "Average mark",
    choose_lang: "Choose language"
};

var _unitStats = {};
var _laneStats = {};
var _lanes = {};
var _msgBuffer = new Array();
var nowTemp = new Date();
var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
var firstTime = 0;

function _init() {
    location.href = "#!statistics";
    //jQuery("head").append(jQuery("<title>CQ - Statistics</title>"));
    document.title = 'CQ - Statistics';

    jQuery('.error').hide();

    onConnect();
}

jQuery("#logoutLink").click(function () {
    console.log("logoutLink was clicked");
    window.location = "/";
    sessionStorage.clear();
    XmppClient.connection.disconnect();
});

function onConnect() {
    var msg = {
        action: "toStatictics"
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

    _msg = msg;
    _lang = msg.lang;

    if (_lang) {
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
        jQuery("#statistics_title").empty();
        jQuery("#monitoring_title").empty();
        jQuery(".line_title").empty();
        jQuery(".all").empty();
        jQuery(".reserve").empty();
        jQuery("#serviced").empty();
        jQuery("#not_came").empty();
        jQuery("#postponed").empty();
        jQuery(".operator_title").empty();
        jQuery("#window").empty();
        jQuery("#call_time").empty();
        jQuery("#start_time").empty();
        jQuery("#ticket_number").empty();
        jQuery("#excell").empty();
        jQuery("#good").empty();
        jQuery("#bad").empty();
        jQuery("#average_mark").empty();

        jQuery(".settings").append(kk_ru.settings);
        jQuery("#help").append(kk_ru.help);
        jQuery("#logoutLink").append(kk_ru.quit);
        jQuery("#statistics_title").append(kk_ru.statistics);
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
        jQuery("#excell").append(kk_ru.excellent);
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

    if (msg.laneStats) {
        _laneStats = msg.laneStats;
        updateLaneStats();
    }
    if (msg.unitStats) {
        _unitStats = msg.unitStats;

        jQuery("#unit-stats tbody").empty();

        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];

            jQuery("#unit-stats tbody").append(
                "<tr data-unit='" + el.username + "'>" +
                "<td><a href='#' onclick='showUnit(\"" + el.username + "\")'>" + el.firstname + " " + el.lastname + "</a></td>" +
                "<td>" + el.window + "</td>" +
                "<td data-stat='tokenAll'>" + el.tokenAll + "</td>" +
                "<td data-stat='countReserved'>" + el.countReserved + "</td>" +
                "<td data-stat='countMarkGood'>" + el.countMarkGood + "</td>" +
                "<td data-stat='countMarkNorm'>" + el.countMarkNorm + "</td>" +
                "<td data-stat='countMarkBad'>" + el.countMarkBad + "</td>" +
                "<td data-stat='markAverage'>" + el.markAverage + "</td>" +
                "</tr>");
        });
    }

    if (_laneStats.length == undefined || _unitStats.length == undefined) {
        _msgBuffer.push(msg);
        return;
    } else {
        if (_msgBuffer.length != 0) {
            jQuery(_msgBuffer).each(function (ix) {
                processMessage(_msgBuffer[ix]);
            });
            _msgBuffer = new Array();
        }
    }
    //processMessage(msg);
}

function processMessage(msg) {
    if (msg.called) {
        var unit = jQuery("tr[data-unit='" + msg.unit + "']");
        unit.find("td[data-type=ticket]").text(msg.ticket);
        unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
        unit.find("td[data-type=called]").text(msg.called);

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];
            if (el.lane == msg.lane) {
                el.called += 1;
            }
        });
        updateLaneStats();
        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];
            if (el.username == msg.unit) {
                el.called = msg.called;
            }
        });
    } else
    if (msg.started) {
        var unit = jQuery("tr[data-unit='" + msg.unit + "']");
        unit.find("td[data-type=ticket]").text(msg.ticket);
        unit.find("td[data-type=lane]").text(_lanes[msg.lane]);
        unit.find("td[data-type=started]").text(msg.started);

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];
            if (el.lane == msg.lane) {
                el.started += 1;
            }
        });
        updateLaneStats();

        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];
            if (el.username == msg.unit) {
                el.started = msg.started;
            }
        });
    } else
    if (msg.ended) {
        var unit = jQuery("tr[data-unit='" + msg.unit + "']");
        unit.find("td[data-type]").text("");

        console.log('msg.ended');

        var startedTime;
        var calledTime;
        var unitRealName;

        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];

            if (el.username == msg.unit) {
                startedTime = el.started;
                calledTime = el.called;
                unitRealName = el.firstname + ' ' + el.lastname;

                el.ended.push({
                    ticket: msg.ticket,
                    tokenId: msg.tokenId,
                    isTicketReserved: msg.isTicketReserved,
                    lane: msg.lane,
                    call: el.called,
                    start: el.started,
                    end: msg.ended,
                    mark: 2
                });
                el.called = undefined;
                el.started = undefined;


                jQuery(el.ended).each(function (jx) {
                    var jel = el.ended[jx];

                    console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

                    if (jel.tokenId == msg.tokenId && jel.tokenId != undefined)
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
                    var countReserved = eval(unit.find("td[data-stat='countReserved']").text());
                    unit.find("td[data-stat='countReserved']").text(++countReserved);
                }

                unit.find("td[data-stat='countMarkGood']").text(msg.countMarkGood);
                unit.find("td[data-stat='countMarkNorm']").text(msg.countMarkNorm);
                unit.find("td[data-stat='countMarkBad']").text(msg.countMarkBad);
                unit.find("td[data-stat='markAverage']").text(msg.markAverage);
            }
        });

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];

            if (el.lane == msg.lane && el.lane != undefined) {
                console.log('showLane laneFound');
                console.log(el.history);
                el.history.push({
                    ticket: msg.ticket,
                    tokenId: msg.tokenId,
                    call: calledTime,
                    start: startedTime,
                    end: msg.ended,
                    mark: 2,
                    isTicketReserved: msg.isTicketReserved,
                    unitRealName: unitRealName
                });

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
    } else
    if (msg.missed) {
        var unit = jQuery("tr[data-unit='" + msg.unit + "']");
        unit.find("td[data-type]").text("");

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];
            if (el.lane == msg.lane) {
                el.missed += 1;
            }
        });
        updateLaneStats();
        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];
            if (el.username == msg.unit) {
                el.missed.push({
                    ticket: msg.ticket,
                    lane: msg.lane,
                    call: el.called,
                    start: el.started,
                    end: msg.missed
                });
                el.called = undefined;
                el.started = undefined;
            }
        });
    } else
    if (msg.transferred) {
        var unit = jQuery("tr[data-unit='" + msg.unit + "']");
        unit.find("td[data-type]").text("");

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];
            if (el.lane == msg.lane) {
                el.transferred += 1;
            }
            if (el.lane == msg.nextLane) {
                el.created += 1;
            }
        });
        updateLaneStats();

        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];
            if (el.username == msg.unit) {
                el.transferred.push({
                    ticket: msg.ticket,
                    lane: msg.lane,
                    targetLane: msg.nextLane,
                    call: el.called,
                    start: el.started,
                    end: msg.transferred
                });
                el.called = undefined;
                el.started = undefined;
            }
        });
    } else
    if (msg.newticket) {
        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];
            if (el.lane == msg.newticket.lane) {
                el.created += 1;
            }
        });
        updateLaneStats();
    } else
    if (msg.smile) {
        console.log('Yahoo! MARK');

        var unit = jQuery("tr[data-unit='" + msg.unit + "']");

        jQuery(_unitStats).each(function (ix) {
            var el = _unitStats[ix];
            if (el.username == msg.unit) {
                console.log('msg.unit is found');

                unit.find("td[data-stat='tokenAll']").text(msg.tokenAll);
                unit.find("td[data-stat='countMarkGood']").text(msg.countMarkGood);
                unit.find("td[data-stat='countMarkNorm']").text(msg.countMarkNorm);
                unit.find("td[data-stat='countMarkBad']").text(msg.countMarkBad);
                unit.find("td[data-stat='markAverage']").text(msg.markAverage);

                jQuery(el.ended).each(function (jx) {
                    var jel = el.ended[jx];

                    console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

                    if (jel.tokenId == msg.tokenId && jel.tokenId != undefined)
                        jel.mark = msg.mark;
                });

                el.tokenAll = msg.tokenAll;
                el.countMarkGood = msg.countMarkGood;
                el.countMarkNorm = msg.countMarkNorm;
                el.countMarkBad = msg.countMarkBad;
                el.markAverage = msg.markAverage;
            }
        });

        jQuery(_laneStats).each(function (ix) {
            var el = _laneStats[ix];

            if (el.lane == msg.lane && el.lane != undefined) {
                jQuery(el.history).each(function (jx) {
                    var jel = el.history[jx];

                    console.log('jel.tokenId == msg.tokenId : ' + jel.tokenId + ' == ' + msg.tokenId);

                    if (jel.tokenId == msg.tokenId && jel.tokenId != undefined)
                        jel.mark = msg.mark;
                });
            }
        });
    }
    console.log(msg);
}



function getUnit(login) {
    var result = {};
    jQuery(_unitStats).each(function (ix) {
        if (_unitStats[ix].username == login) {
            result = _unitStats[ix];
        }
    });
    return result;
}


function updateLaneStats() {
    jQuery("#lane-stats tbody").empty();
    jQuery(_laneStats).each(function (ix) {
        var el = _laneStats[ix];
        _lanes[el.lane] = el.laneText;

        jQuery("#lane-stats tbody").append("<tr data-lane='" + el.lane + "'><td><a href='javascript:showLane(\"" + el.lane + "\")' >" + el.laneText + "</a></td><td>" + el.created + "</td><td>" + el.countReserved + "</td><td>" + el.ended + "</td><td>" + el.missed + "</td><td>" + el.transferred + "</td></tr>");
    });
}


function showLane(lanename) {
    // console.log(_laneStats);
    jQuery(_laneStats).each(function (ix) {
        var el = _laneStats[ix];

        if (lanename == el.lane && el.lane != undefined) {
            jQuery('#laneModal legend div span#laneName').text(kk_ru.line + ': ' + el.laneText);

            jQuery("#laneModal tbody").empty();

            console.log('showLane laneFound');
            console.log(el.history);

            for (var prop in el.history) {
                var jel = el.history[prop];

                console.log(jel);
                /*
call: "09:54:00"
end: "09:54:02"
isTicketReserved: false
mark: 2
start: "09:54:01"
ticket: "001"
tokenId: 24

<th>Оператор</th>
<th>Билет</th>
<th></th>
<th>Начало</th>
<th>Конец</th>
<th>Оценка</th>
*/
                jQuery("#laneModal tbody").append("<tr>");
                jQuery("#laneModal tbody").append("<td>" + jel.unitRealName + "</td>");

                var isReservedText = (jel.isTicketReserved ? ' ' + kk_ru.reserve : '')
                jQuery("#laneModal tbody").append("<td>" + jel.ticket + isReservedText + "</td>");
                if (jel.start)
                    jQuery("#laneModal tbody").append("<td>" + jel.start + "</td><td>" + jel.end + "</td>");

                var markText = '';

                switch (jel.mark) {
                case 0:
                    markText = '';
                    break;
                case 5:
                    markText = kk_ru.excellent;
                    break;
                case 3:
                    markText = kk_ru.good;
                    break;
                case 1:
                    markText = kk_ru.bad;
                    break;
                }
                jQuery("#laneModal tbody").append("<td>" + markText + "</td>");

                jQuery("#laneModal tbody").append("</tr>");
            }
            $('#laneModal').foundation('reveal', 'open');
        }
    });
    return false;
}


function showUnit(username) {
    jQuery(_unitStats).each(function (ix) {
        var el = _unitStats[ix];
        if (username.indexOf(el.username) != -1) {
            jQuery('#unitModal legend div span#unitRealname').text(kk_ru.operator + ': ' + el.firstname + ' ' + el.lastname);
            jQuery('#unitModal legend div span#unitUsername').text("Логин: " + el.username);

            jQuery("#unitModal ul.unitLanes").empty();
            jQuery(el.lanes).each(function (ixx) {
                jQuery("#unitModal ul.unitLanes").append("<li><span class='label'>" + el.lanes[ixx].laneText + "</span></li>");
            });
            $tbody = jQuery("#unitModal tbody");

            $tbody.empty();
            jQuery(el.ended).each(function (jx) {
                var jel = el.ended[jx];

                var laneText = jel.laneText;

                if (laneText == undefined) {
                    for (var prop in _lanes)
                        if (prop == jel.lane) {
                            laneText = _lanes[prop];
                            break;
                        }
                }
                if (jel.start) {
                    $tbody.append("<tr>");

                    $tbody.append("<td>" + laneText + "</td>");

                    var isReservedText = (jel.isTicketReserved ? ' ' + kk_ru.reserve : '');
                    console.log('jel.isTicketReserved = ' + jel.isTicketReserved + ' isReservedText = ' + isReservedText);
                    $tbody.append("<td>" + jel.ticket + isReservedText + "</td>");
                    $tbody.append("<td>" + jel.start + "</td><td>" + jel.end + "</td>");

                    var markText = '';

                    switch (jel.mark) {
                    case 0:
                        markText = '';
                        break;
                    case 5:
                        markText = kk_ru.excellent;
                        break;
                    case 3:
                        markText = kk_ru.good;
                        break;
                    case 1:
                        markText = kk_ru.bad;
                        break;
                    }
                    $tbody.append("<td>" + markText + "</td></tr>");
                }
            });
            $('#unitModal').foundation('reveal', 'open');
        }
    });
    return false;
}

function showRepresentDate() {
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
    var checkin = $('#datepickerFrom').fdatepicker({
        format: 'dd.mm.yyyy'
    }).on('changeDate', function (ev) {
        if (ev.date.valueOf() > checkout.date.valueOf()) {
            var newDate = new Date(ev.date)
            newDate.setDate(newDate.getDate() + 1);
            checkout.setValue(newDate);
        }
        checkin.hide();
        $('#datepickerTo')[0].focus();
    }).data('datepicker');
    var checkout = $('#datepickerTo').fdatepicker({
        format: 'dd.mm.yyyy',
        onRender: function (date) {
            return date.valueOf() <= checkin.date.valueOf() ? 'disabled' : '';
        }
    }).on('changeDate', function (ev) {
        checkout.hide();
    }).data('datepicker');
    if (firstTime == 0) {
        $("body").append(jQuery("<script src='javascripts/foundation/foundation.datepicker.js'></script"));
    }

    $('#representDateModal').foundation('reveal', 'open');
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
        action: "monitor_representDate",
        dateFrom: dateFrom,
        dateTo: dateTo
    };
    XmppClient.send("queue", JSON.stringify(msg));
}

function goToMonitoring() {
    console.log('gotomonitoring');
    var lang = _lang;
    jQuery("#body").load("/monitor2.html", function (html) {
        _initFromStatistics(lang);
        //_init();
        jQuery("#body").show();
    });
}

function getSettings() {
    jQuery('#messageBody').html(
        '<p>' + kk_ru.choose_lang + '</p>' +
        '<label for="ru_lang"><input type="radio" id="ru_lang" name="lang" value="ru" />Русский</label>' +
        '<label for="kk_lang"><input type="radio" id="kk_lang" name="lang" value="kk" />Казахский</label>' +
        '<label for="en_lang"><input type="radio" id="en_lang" name="lang" value="en" />English</label>');
    $('#message').foundation('reveal', 'open');
}

function change_lang() {
    var inputs = document.getElementsByName("lang");
    for (var i = 0; i < inputs.length; i++) {
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
        action: "changeLang",
        user: sessionStorage.jid,
        language: _lang
    }));
    //locale(_lang);
}

function _onStatus(status) {
    if (status == "CONNECTED" || status == "ATTACHED") {
        onConnect();
    }
    if (status == "DISCONNECTED") {
        console.log("Disconnected");
    }
}

//Value parameter - required. All other parameters are optional.

function isDate(value, sepVal, dayIdx, monthIdx, yearIdx) {
    try {
        value = value.replace(/-/g, "/").replace(/\./g, "/");
        sepVal = (sepVal === undefined ? "/" : sepVal.replace(/-/g, "/").replace(/\./g, "/"));

        var SplitValue = value.split(sepVal);
        if (SplitValue.length != 3) {
            return false;
        }

        //Auto detection of indexes
        if (dayIdx === undefined || monthIdx === undefined || yearIdx === undefined) {
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

        //Change the below values to determine which format of date you wish to check. It is set to dd/mm/yyyy by default.
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
        if (OK && !(SplitValue[MonthIndex].length == 1 || SplitValue[MonthIndex].length == 2)) {
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
            var MonthDays = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

            if (OK = (Month <= 12 && Month > 0)) {

                var LeapYear = (Year & 3) == 0 && ((Year % 25) != 0 || (Year & 15) == 0);
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

function saveLaneStat() {
    var textUnit = jQuery("#representDate").text() + "\n" + kk_ru.line + ';' + kk_ru.all + ';' + kk_ru.reserve + ';' + kk_ru.serviced + ';' + kk_ru.not_came + ';' + kk_ru.postponed + "\n";

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

    var textUnit = jQuery("#representDate").text() + "\n" + kk_ru.operator + ';' + kk_ru.window + ';' + kk_ru.all + ';' + kk_ru.reserve + ';' + kk_ru.excellent + ';' + kk_ru.good + ';' + kk_ru.bad + ';' + kk_ru.average + "\n";

    jQuery(_unitStats).each(function (ix) {
        var el = _unitStats[ix];
        textUnit += el.firstname + " " + el.lastname + ";" +
            el.window + ";" +
            el.tokenAll + ";" +
            el.countReserved + ";" +
            el.countMarkGood + ";" +
            el.countMarkNorm + ";" +
            el.countMarkBad + ";" +
            el.markAverage + "\n";
    });

    var blob = new Blob([textUnit], {
        type: "text/plain;charset=Unicode",
    });
    saveAs(blob, "statistics_unit.csv");
    return false;
}