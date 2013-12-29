var $head = jQuery("head"),
	$body = jQuery("body");

$head.append("<link href='stylesheets/reserve/reservation.css' media='screen, projection' rel='stylesheet'>");
$head.append("<link href='http://fonts.googleapis.com/css?family=Ubuntu&amp;subset=latin,cyrillic' rel='stylesheet' type='text/css'>");
$head.append("<title>Войдите в систему</title>");
$head.append('<link rel="stylesheet" href="/js/jquery-ui/development-bundle/themes/base/minified/jquery.ui.datepicker.min.css" />');
$head.append('<link rel="stylesheet" href="/js/jquery-ui/development-bundle/themes/base/minified/jquery.ui.core.min.css" />');
$head.append('<link rel="stylesheet" href="/js/jquery-ui/development-bundle/themes/base/minified/jquery.ui.theme.min.css" />');

$body.append("<body>"+
"<div class='wrapper'>"+
"<ul id='reservation-steps'>"+
"<li id='step1' class='reservation-step'>"+
  "<div class='content'>"+
    "<h1 class='step-title'>Бронирование</h1>"+
    "<h2 class='step-title-small'>Выберите филиал</h2>"+

      "<select id='step1_select' class='reservation_modal-select' style='width:258px;'>"+                
      "</select>"+
    "<input class='reservation_modal-submit' id='next1' type='submit' value='Вперед'>"+
    "<p class='reservation_modal-help'>"+
      "<a href='#'>"+
        "Помощь"+
      "</a>"+
      "<br>"+
    "</p>"+
  "</div>"+
"</li>"+

"<li id='step2' class='reservation-step'>"+
  "<div class='content'>"+
    "<h1 class='step-title'>Ваши данные</h1>"+
    "<form data-validate='parsley' id='step2_form'>"+
        "<input class='reservation_modal-input required' type='text' placeholder='Имя' data-required='true' data-error-message='Этот пункт обязателен!'/ name='firstname' id='firstname'/>"+
        "<input class='reservation_modal-input' type='text' placeholder='Фамилия' data-required='true' data-error-message='Этот пункт обязателен!'/ name='lastname' id='lastname'/>"+
        "<input class='reservation_modal-input' type='text' placeholder='Отчество' data-required='true' data-error-message='Этот пункт обязателен!'/ name='middlename' id='middlename'/>"+
        "<input class='reservation_modal-input' type='text' placeholder='Номер телефона' data-required='true' name='iin_number' data-type='number' id='iinInput' data-error-message='Введите номер телефона'/>"+

        "<input id='next2' type='submit' class='reservation_modal-submit' value='Дальше'>"+
      "</form>"+
    "<input id='back2' type='submit' class='reservation_modal-submit-additional' value='Назад'>"+
    "<p class='reservation_modal-help'>"+
      "<a href='#'>"+
        "Помощь"+
      "</a>"+
      "<br>"+
    "</p>"+
  "</div>"+
"</li>"+

"<li id='step3' class='reservation-step'>"+
  "<div class='content'>"+
    "<h1 class='step-title'>Выберите дату</h1>"+
		"<p>"+
		"<input type='text' id='datepicker'  placeholder='Дата' name='datepicker' class='reservation_modal-input'/>"+
		"<select id='time' placeholder='Время' class='reservation_modal-input'></select>"+
	"</p>"+
	"<label class='error' for='datepicker' id='datepicker_error'></label>"+
    "<input class='reservation_modal-submit' id='next3' type='submit' value='Дальше'>"+
    "<input id='back3' type='submit' class='reservation_modal-submit-additional' value='Назад'>"+
    "<p class='reservation_modal-help'>"+
      "<a href='#'>"+
        "Помощь"+
      "</a>"+
      "<br>"+
    "</p>"+
  "</div>"+
"</li>"+

"<li id='step4' class='reservation-step'>"+
  "<div class='content'>"+
    "<h1 class='step-title'>Выберите услуги</h1>"+
    "<div id='lanes'>"+
    "</div>"+
    
    "<input class='reservation_modal-submit' id='next4' onclick='reserveTicket();' type='submit' value='Бронировать'>"+
    "<input id='back4' type='submit' class='reservation_modal-submit-additional' value='Назад'>"+
    "<p class='reservation_modal-help'>"+
      "<a href='#'>"+
        "Помощь"+
      "</a>"+
      "<br>"+
    "</p>"+
  "</div>"+
"</li>"+

"</ul>"+
"</div>");

jQuery("#reservation-steps").hide();

$body.append('<script type="text/javascript" src="/javascripts/reserve/main-ck.js"></script>');
$body.append('<script src="/js/jquery-ui/js/jquery-ui-1.10.1.custom.min.js"></script>');
$body.append('<script src="/js/jquery-ui/development-bundle/ui/minified/i18n/jquery.ui.datepicker-ru.min.js"></script>');
$body.append('<script src="/js/jquery/inputmask/jquery.inputmask.js" type="text/javascript"></script>');
$body.append('<script src="/js/jquery/inputmask/jquery.inputmask.extensions.js" type="text/javascript"></script>');
$body.append('<script src="/js/jquery/inputmask/jquery.inputmask.numeric.extensions.js" type="text/javascript"></script>');

var lanes = "";
var reserveButton;
var scene = 'group';
var group = null;

//var myHost = "localhost:9090";
var myHost = "cq.b2e.kz";
var iin = null;
var _weekend_days = '0';

reserveButton = jQuery("#reserveButton");
_date = null;
_month = null;
_time = null;
_start = null;
_obj = null;
_weekend = null;
_notWorkingWeekends = null;
var _not_working_holidays = new Array();

var dataString = 'request=group';
jQuery(document).ready(function(){
jQuery.ajax
({
	type: 'POST',
	url: 'http://'+ myHost +'/plugins/beequeue/reservation',
	dataType: 'json',
	data: dataString,
	success: function(data) {
		prepareGroup(data);
	}
});

$("#step1_select").select2({placeholder: "Выберите филиал"});

var next1 = $("#next1"),
      next2 = $("#next2"),
      next3 = $("#next3"),
      next4 = $("#next4"),
      back2 = $("#back2"),
      back3 = $("#back3"),
      back4 = $("#back4"),
      step1 = $("#step1"),
      step2 = $("#step2"),
      step2_form = $("#step2_form"),
      step3 = $("#step3"),
      step4 = $("#step4");


next1.on('click',function(){
    step1.animate({'margin-left':'-470','opacity':'0'});
	group = jQuery('#step1_select').val();
	console.log(group);
	scene = 'iin';
	retrievePageContent();
});

back2.on('click',function(){
    step1.animate({'margin-left':'0','opacity':'1'});
    scene = 'group';
});

back3.on('click',function(){
    step2.animate({'margin-left':'0','opacity':'1'});
    scene = 'date';
});

back4.on('click',function(){
    step3.animate({'margin-left':'0','opacity':'1'});
	scene = 'iin';
});

next2.on('click',function(){
  step2_form.parsley( 'validate' );
  if(step2_form.parsley( 'isValid' )==true){
    step2.animate({'margin-left':'-470','opacity':'0'});
  };
	iin = jQuery('#iinInput').val();
	console.log('iin ' + iin);
	scene = 'date';
	return false;
})

next3.on('click',function(){
  step3.animate({'margin-left':'-470','opacity':'0'});
	var date = jQuery("#datepicker").val();
	var time = jQuery('#time').val();
	if(iin != null){
		if ( iin.length == 0 ) {
			jQuery("label#iinInput_error").html('Необходимое поле.');
			jQuery("label#iinInput_error").show();
			jQuery("input#iinInput").focus();
			return;
		}

		if ( iin.indexOf('_') != -1 ) {
			jQuery("label#iinInput_error").html('Неправильно введено.');
			jQuery("label#iinInput_error").show();
			jQuery("input#iinInput").focus();
			return;
		}

		if ( !isDate(date, '.') ) {
			jQuery("label#datepicker_error").html('Некорректная дата.');
			jQuery("label#datepicker_error").show();
			jQuery("input#datepicker").focus();
			return;
		}
	}else{
		back2.click();
	} 
});

});
  
  function prepareGroup (obj) {
	  	_obj = obj;
		scene = 'group';
		console.log(obj);
		var groups = obj.groups;

		for (var prop in groups) {
			group = groups[prop];
			$('#step1_select').append("<option value='"+group.name+"'>" + group.address + "</option>");
		}
	}

	function retrievePageContent() {
		var now = new Date();
		var d = now.getDate();
		var m = now.getMonth() + 1;
		var y = now.getFullYear();

		if (d < 10)
			d = '0' + d;

		if (m < 10)
			m = '0' + m;
		
		var date = d + '.' + m + '.' + y;

		var dataString = 'request=contentPage' +
		'&date=' + date + 
		'&group=' + group;

		jQuery.ajax
		({
			type: 'POST',
			url: 'http://'+ myHost +'/plugins/beequeue/reservation',
			dataType: 'json',
			data: dataString,
			success: function(data) {
				preparePage(data);
			}
		});
	}

	function preparePage (obj) {
		_obj = obj;
		
		notWorkingHolidays = _obj.notWorkingHolidays;
		
		_not_working_holidays[0] = "";
		
		if(notWorkingHolidays != null && notWorkingHolidays.length > 0){
			for(var i = 0; i < notWorkingHolidays.length; i++){
				var text_date = notWorkingHolidays[i].holidays;
				_not_working_holidays[i+1] = text_date.substring(0, 10);
			}
		}
		
		_notWorkingWeekends = _obj.notWorkingWeekends;
		console.log(_notWorkingWeekends);
		if(_notWorkingWeekends != null && _notWorkingWeekends.length > 0){
			_weekend_days = _notWorkingWeekends[0].weekends;
		}
		
		prepareTime(obj.time, obj.weekend);
		prepareLanes(obj.lanes);
	}
	
	function prepareHours(){
		
		var time = _time;
		console.log(time.month + " ++ " + $("#datepicker").datepicker("getDate").getMonth());
		console.log(time.date + " ++ " + $("#datepicker").datepicker("getDate").getDate());
		
		var startArray = time.start.split(":");
		var start = parseInt( startArray[0] );
		
		if(time.month != $("#datepicker").datepicker("getDate").getMonth() || time.date != $("#datepicker").datepicker("getDate").getDate())
		{
			console.log("different");
			start = 8;
			startArray[1] = "30";
			console.log("different closed");
		}
		console.log(start);
		
		var endArray = time.end.split(":");
		var end = parseInt( endArray[0] );
		console.log(end);
		jQuery("#time").empty();

		$('#timepicker').empty();
		$("#timepicker").pickatime({
			  interval: 30,
			  min: [start,00],
			  max: [18,30],
			  format: 'Запись на H:i',
			  clear: 'Очистить'
			});

		
		_start = start + ""+startArray[1];
		
		if(start <= 19){
		if(_start!="1830"){
		var jump = true;
		for (var i=start; i<end; i++) {
			if(jump==false){
				break;
			}
			for (var j=0; j<2; j++) {
				if (i == start && j==0 && startArray[1] == '30')
					continue;

				var time1 = i + ':';

				if (i<10)
					time1 = '0' + i + ':';
					
				if (j == 0)
					time1 += '00';
				else
					time1 += '30';

				var x = i + j;
				var time2 = x + ':';

				if (x<10)
					time2 = '0' + x + ':';
					
				if (j == 1)
					time2 += '00';
				else
					time2 += '30';
				
				for(var k = 0; k < _weekend.length; k++){
					var holiday = _weekend[k];
					tt = time1.split(":");
					var h = +holiday.start.substring(11,13);
					var m = +holiday.start.substring(14,16);
					if($("#datepicker").datepicker("getDate").getMonth()+1==holiday.start.substring(5,7) &&
							$("#datepicker").datepicker("getDate").getDate()==holiday.start.substring(8,10) &&
							+tt[0]==h && tt[1]==m)
					{
						jump = false;
						console.log("jump = false");
						break;
					}
				}
				
				if(jump==true){
					jQuery("#time").append("<option style='color:black;' value='"+time1+"'>" + time1 + " - " + time2 + "</option>");
				}
			}
		}

/*		if (endArray[1] == '30') {
			var time1 = end + ':';
			if (end<10)
				time1 = '0' + end + ':';

			var time2 = time1;
			time1 += '00';
			time2 += '30';
			
			jQuery("#time").append("<option value='"+time1+"'>" + time1 + " - " + time2 + "</option>");
		}*/
	}
	}
	}
	
	function prepareTime (time, weekend) {
		_weekend = weekend;
		_time = time;
		scene = 'iin';
		jQuery("#iinInput").inputmask({ "mask": "+7(799) 999-9999"});
		//$("#phone").mask("(999) 999-9999");
		jQuery( "#datepicker" ).datepicker({ 
			minDate: 0, 
			maxDate: "+10D",
			onSelect: function() {
			     prepareHours();
			},
			beforeShowDay: noWeekends
		});
		//jQuery( "#datepicker" ).datepicker( "option", jQuery.datepicker.regional[ "ru" ] );
		jQuery( "#datepicker" ).datepicker( "option", "dateFormat", "dd.mm.yy" );
	}

	function noWeekends(date) {
		if(date.getDay() == 6 && _weekend_days.indexOf('6')>-1)
	      	return [false, ''];
		else if(date.getDay() == 5 && _weekend_days.indexOf('5')>-1)
	      	return [false, ''];
		else if(date.getDay() == 4 && _weekend_days.indexOf('4')>-1)
	      	return [false, ''];
		else if(date.getDay() == 3 && _weekend_days.indexOf('3')>-1)
	      	return [false, ''];
		else if(date.getDay() == 2 && _weekend_days.indexOf('2')>-1)
	      	return [false, ''];
		else if(date.getDay() == 1 && _weekend_days.indexOf('1')>-1)
	      	return [false, ''];
		else if(date.getDay() == 0 && _weekend_days.indexOf('7')>-1)
			return [false, ''];
		
		var loc_year = ""+date.getFullYear();
		var loc_month = ""+(date.getMonth()+1);
		var loc_date = ""+date.getDate();
		
		if(loc_month.length == 1)
			loc_month = "0" + loc_month;
		
		if(loc_date.length == 1)
			loc_date = "0" + loc_date;
		
		var ymd = loc_year + "-" + loc_month + "-" + loc_date;
		if ($.inArray(ymd, _not_working_holidays) > 0) {
		    return [false,""];
		}
		  
		return [true, ''];
	}
	
	function prepareLanes(lanesObj) {
		console.log(lanesObj);
		jQuery("#lanes").empty();
		console.log('lanes--------------------------------------');
		jQuery(lanesObj).each(function(ix){
			var el = lanesObj[ix];
			$('#lanes').append("<input id='"+ el.id +"' class='icheck-checkbox' type='checkbox'><label>"+el.ru+"</label>");
			//jQuery("#lanes").append("<div class='icheckbox_line-aero'><input class='icheck-checkbox' type='checkbox' checked='' style='position: absolute; opacity: 0;'><div class='icheck_line-icon'></div>"+el.ru+"<ins class='iCheck-helper' style='position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background-color: rgb(255, 255, 255); border: 0px; opacity: 0; background-position: initial initial; background-repeat: initial initial;'></ins></div>");
			
			$('#'+el.id).on('ifChanged', function(event){
				if(lanes.indexOf(el.id+",") >= 0) {
					lanes = lanes.replace(el.id+",","");
				} else {
					lanes += el.id+",";
				}
				console.log(lanes);
			});
			
		});

		
		$('.icheck-checkbox').each(function(){
		    var self = $(this),
		      label = self.next(),
		      label_text = label.text();
		    //onclick=\"toggleLaneType('"+el.id+"')\"
		    
		    label.remove();
		    self.iCheck({
		      checkboxClass: 'icheckbox_line-aero',
		      radioClass: 'iradio_line-aero',
		      insert: '<div class="icheck_line-icon"></div>'  +label_text
		    });
		  });

		reserveButton.addClass("disabled");
	}

	function reserveTicket() {
		console.log('reserveTicket');
		
		var iin = "7"+jQuery('#iinInput').val();
		var date = jQuery("#datepicker").val();
		var time = jQuery('#time').val();

		lanes = lanes.substring(0, lanes.length - 1);
		console.log("lanes " + lanes);
		
	//  URL GOTO = "http://localhost:9090/plugins/beequeue/reservation?time=14:00&date=05.03.2013&id=000111222031&lanes=a,b&group=atf"
		var dataString = 'request=reserveTicket'+
		'&time=' + time +
		'&date=' + date + 
		'&id=' + iin + 
		'&lanes=' + lanes +
		'&group=' + group;

		lanes = "";

		console.log('dataString ' + dataString);
		jQuery.ajax
		({
			type: 'POST',
			url: 'http://'+ myHost +'/plugins/beequeue/reservation',
			dataType: 'json',
			data: dataString,
			success: function(data) {
				console.log(data);
				refresh(data);
			}
		});
		jQuery('#reservation-steps').fadeOut();
	}

	function refresh (obj) {
		scene = 'group';
		back4.click();
		back3.click();
		back2.click();
		
		if (obj.busy) {
			alert(obj.busy);
		}
		else if(obj.id) {
			$('#firstname').val('');
			$('#lastname').val('');
			$('#middlename').val('');
			$('#iinInput').val('');
			$('#datepicker').val('');
			alert("Вы успешно забронированы");
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
	 
	        //Auto  detection of indexes
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
	 
	        var OK = true;
	        if (!(SplitValue[DayIndex].length == 1 || SplitValue[DayIndex].length == 2)) {
	            OK = false;
	        }
	        if (OK && !(SplitValue[MonthIndex].length == 1 || SplitValue[MonthIndex].length == 2)) {
	            OK = false;
	        }
	        if (OK && SplitValue[YearIndex].length != 4) {
	            OK = false;
	        }
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
	        return OK;
	    }
	    catch (e) {
	        return false;
	    }
	}
	
	
