var _ru = {
	reservation_title: 'Бронировали ли вы очередь?',
	yes: 'Да',
	kiosk_backspace: 'Удалить',
	attention: 'Внимание!',
	lead:'Вашего ИИН не оказалось в нашей базе. Проверьте правильно ли введен номер.',
	kiosk_iin_input:'Введите ваш ИИН',
	ticketCreateDateMessage:'Время:',
	laneTypesMessage:'Услуги:',
	waitingClientsPrintMessage:'Количество клиентов перед Вами: ',
	no: 'Нет'
};
var _en = {
	reservation_title: 'Did you reserve the ticket?',
	yes: 'Yes',
	kiosk_backspace: 'Clear',
	attention: 'Attention!',
	lead:'Your IIN wasn\'t found. Check the number',
	kiosk_iin_input:'Write your IIN',
	ticketCreateDateMessage:'Time ',
	laneTypesMessage:'Services ',
	waitingClientsPrintMessage:'Number of clients before you: ',	
	no: 'No'
	};
var _kk = {
	reservation_title: 'Бронь арқылы ма?',
	yes: 'Иә',
	kiosk_backspace: 'Жою',
	attention: 'Назар аударыңыз!',
	lead:'Сіздің ЖСН базадан табылмады. Нөмеріңіздің нақты дұрыс екенін тексеріңіз',
	kiosk_iin_input:'ЖСН енгізіңіз',
	ticketCreateDateMessage:'Уақыт',
	laneTypesMessage:'Қызметтер',
	waitingClientsPrintMessage:'Сіздің алдыңыздағы клиент саны: ',
	no: 'Жоқ'
	};
var iinFake,
      iin = null,
        lanes = "",
        $lanes = $("#kiosk-services"),

        _lanes = null,
        _reservations = new Array(),
        _laneTree = null,
        _group = {},
        _lang = null,
        _data = null;
		var firstTime = 0;
	      
      function toggleLaneType(laneTypeName){
        kiosk_services = $("#kiosk-services a");
    	IDlaneTypeName = $("#"+laneTypeName);
    		  
	    var $this = IDlaneTypeName;
        $this.toggleClass('active');

        
        $this.find('.tick-pic, figure').fadeToggle(99);
	    if(lanes.indexOf(laneTypeName+";") >= 0){
            lanes = lanes.replace(laneTypeName+";", "");
        } else {
            lanes += laneTypeName+";";
        }
	    clearLanes();
        var splittedLanes = lanes.split(";");
        var counter = 0;
        jQuery(splittedLanes).each(function(index){
            counter++;     
           	jQuery("#laneNumber" + splittedLanes[index]).text(counter);
            jQuery("#laneNumber" + splittedLanes[index]).show();
        });
        console.log(counter);
        if(counter==1){
            print_services.fadeOut();
        }else{
            print_services.fadeIn();
        }
      	
    }

      function _initFromLang(data, lang) { //MUST BE CALLED WHEN LANGUAGE BUTTON CLICKED
    	 console.log('lang')
    	  $lanes.empty();
        _data = data;
        _lang = lang;
          $(_lanes).each(function(ix) {
            var el = _lanes[ix];
            if (_lang == 'en-lang') {
                $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<div class='tick-pic ui-tick'></div><span>" + el.en + "</span><figure style='display:none' id='laneNumber"+el.id +"'></figure></a></li>");
            } else if (_lang == 'kk-lang') {
                $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<div class='tick-pic ui-tick'></div><span>" + el.kk + "</span><figure style='display:none' id='laneNumber"+el.id +"'></figure></a></li>");
            } else {
                $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<div class='tick-pic ui-tick'></div><span>" + el.ru + "</span><figure style='display:none' id='laneNumber"+el.id +"'></figure></a></li>");
            }
          });
        setTimeout(function() {
          $("#kiosk-step-2").hide();
          $("#kiosk-step-3").hide();
          $("#kiosk-step-iin").hide();
          $("#kiosk-step-1").show();
        }, 120000);
        if(_lang=="kk-lang"){
    		kk_ru = _kk;
    	}else if(_lang=="ru-lang"){
    		kk_ru = _ru;
    	}else if(_lang=="en-lang"){
    		kk_ru = _en
    	}
        $('#reservation_title').text(kk_ru.reservation_title);
        $('#yes').text(kk_ru.yes);
        $('#no').text(kk_ru.no);
        $('#kiosk-backspace').text(kk_ru.kiosk_backspace);
        $('#attention').text(kk_ru.attention);
        $('#lead').text(kk_ru.lead);
        $('#kiosk-iin-input')[0].placeholder=kk_ru.kiosk_iin_input;
      }
      
      function cleariin(){
    	  print_iin.fadeOut();
    	  $('#kiosk-iin-input').empty();
      }
      
      function clearAll(){
    	  clearLanes();
    	  cleariin();
    	  clearActiveLanes();
      }
      function clearActiveLanes(){
    	  print_services.fadeOut();
    	  lanes = "";
    	  jQuery(_lanes).each(function(index){
    		    jQuery("#"+_lanes[index].id).removeClass("active");
    	        jQuery("#"+_lanes[index].id).find('.tick-pic, figure').hide();
    	  });
    	  clearLanes();
      }
      function clearLanes(){
    	  jQuery(_lanes).each(function(index){
    		  jQuery("#laneNumber" + _lanes[index].id).empty();  
    		  jQuery("#laneNumber" + _lanes[index].id).hide();   
    	  });
      }
      
      function _init(data) {
    	  console.log('init');
    	_data = data;


        /* it needs if logo exists
        if (data.logo) {
          $('#kioskLogo').attr('src', data.logo);
        }
        if (data.ticketLogo) {
          $('#printLogo').attr('src', data.ticketLogo); //'http://localhost:7070/img/kiosk/tson_logo.png')
        }
        */
        if (data.lanes) {
          _lanes = data.lanes;
          console.log(_lanes);
        }
        if (data.group) {
          _group = data.group;
        }
        if (data.reservations) {
          _reservations = data.reservations;
        }
        iinFake = $("#kiosk-iin-input");

        $("#kk-lang").click(function() {
        	_initFromLang(data, 'kk-lang');
            step1.fadeOut(300, function() {
              step2.fadeIn(300);
            });
         });
          $('#ru-lang').click(function() {
          	  _initFromLang(data, 'ru-lang');
              step1.fadeOut(300, function() {
                step2.fadeIn(300);
              });
            });
          $("#en-lang").click(function() {
        	  _initFromLang(data, 'en-lang');
              step1.fadeOut(300, function() {
                step2.fadeIn(300);
              });
          });
          console.log('head_---------------------------------------');
          jQuery("head").empty();
          document.title = 'Киоск CloudQueue';
          jQuery("head").append(jQuery("<meta charset='utf8'><meta content='width=device-width' name='viewport'><link href='stylesheets/kiosk.css' media='print,screen' rel='stylesheet'><link href='http://fonts.googleapis.com/css?family=Ubuntu&amp;subset=latin,cyrillic' rel='stylesheet' type='text/css'>"));
      }

      function getLane(lane) {
        var result = null;
        $(_lanes).each(function(ix) {
          var el = _lanes[ix];
          if (el.id == lane) {
            result = el;
          }
        });
        return result;
      }
      
      
      function _onMessage(text) {
        console.log("_onMessage:" + text);
        var msg = {};
        try {

          msg = JSON.parse(text);
        } catch (e) {
          console.warn("Kiosk got non-JSON message:" + text);
        }
        console.log(msg);
        if (msg.ticket) {
          $("#ticketCode").text(msg.ticket.code);
          var clientId = iinFake.text();
          if (clientId.length == 0) {
            clientId = "000000000000";
          }
          if(clientId!='000000000000'){
        	  $("#clientIdMessage").text("ID:");
              $("#clientId").text(clientId);
              $('#cli').show();
          }else{
        	  $('#cli').hide();
          }

          $("#ticketCreateDateMessage").text(kk_ru.ticketCreateDateMessage);
          var date = new Date();
          $("#ticketCreateDate").text(date.toJSON().split("T")[0] + " " + date.toTimeString().split(" ")[0]);
          $("#laneTypesMessage").text(kk_ru.laneTypesMessage);

          var a = "";

          $(msg.ticket.lanes).each(function(ix) {
            if (msg.ticket.lanes[ix].length > 0) {
              var el = msg.ticket.lanes[ix]

              var waitingClients = 0;
              for (var prop in _lanes) {
                lane = _lanes[prop];

                if (lane.id == getLane(el).id) {
                  if (msg.ticket.isReserved) waitingClients = lane.reserved - 1;
                  else waitingClients = lane.count + lane.reserved - 1;
                }
              }
              if(_lang=='en-lang'){
            	  a += getLane(el).en + " (" + waitingClients +")" + ",";
              }else if(_lang=='kk-lang'){
            	  a += getLane(el).kk + " (" + waitingClients +")" + ",";
              }else{
            	  a += getLane(el).ru + " (" + waitingClients +")" + ",";
              }
              //a += getLane(el).ru + " (" + waitingClients;
              //a += "<img src='img/rsz_usestrs.png' width='15px' height='15px'/>";
              //a += ")" + ", "
            }
          });
          //a = a.substr(0, a.length - 2);
          //$("#laneTypes").append(a);
          
          a = a.substr(0,a.length-1);
          console.log(a);
          $("#laneTypes").text(a+'');
          var waitingClients = 0;

          $(msg.ticket.lanes).each(function(ix) {
            var b = msg.ticket.lanes[ix];

            $(_lanes).each(function(jx) {
              var el = _lanes[jx];
              if (el.id == b) {
                console.log(el.count + ' / ' + el.reserved);
                if (msg.ticket.isReserved) {
                  waitingClients = waitingClients + el.reserved - 1;

                  var index = -1;
                  for (var prop in _reservations) {
                    var reservation = _reservations[prop];

                    if (reservation.id == msg.ticket.clientIin && msg.ticket.clientIin != undefined) {
                      index = prop;
                      break;
                    }
                  }
                  if (index != -1) _reservations.splice(index, 1);
                } else waitingClients = waitingClients + el.count + el.reserved - 1;
              }
            });
          });

          $("#waitingClientsPrintMessage").text(kk_ru.waitingClientsPrintMessage + waitingClients);
          

          window.print();
          $("#kiosk-step-2").hide();
          $("#kiosk-step-3").hide();
          setTimeout(function() {
            $("#kiosk-step-1").show();
          }, 500);
        } else if (msg.pubsub) {
          console.log("**** PUBSUB ****");
          console.log(msg);

          $(msg.lanes).each(function(ix) {
            var a = msg.lanes[ix];
            $(_lanes).each(function(jx) {
              var el = _lanes[jx];
              if (el.id == a.id) {
                el.count = a.count;
                el.reserved = a.reserved;
                _lanes[jx] = el;
              }
            });
          });

          if (msg.reservation) {
            _reservations.push(msg.reservation);
          }
        }
      }

      function onConnect() {
    	  firstTime++;
        setTimeout(function() {
          $(".panel").fadeIn("slow");
          nextScene("lanes");
        }, 600);
        console.log(XmppClient.connection.sid);
      }

      function _onStatus(status) {
    	if (status == "CONNECTED" || status == "ATTACHED") {
        	onConnect();
        }
        if (status == "DISCONNECTED") {
        	$(".panel").fadeOut("slow");
        }
      }

      function printTicket() {    	  
    	console.log('print ticket')
    	if(iinFake.text()==''){
            if (lanes && lanes.length > 0) {
            	console.log('enqueue print');
                enqueue(_lang);
            }
    	}else{
            _reservations.forEach(function(reservation) {
              if (reservation.id == iinFake.text()) {
                console.log(reservation.id + ' found');
                iin = $("#kiosk-iin-input").text();
                reservationIsFound = true;
              }
            });
            var reservationIsFound = false;
            $(_reservations).each(function(ix) {
              var el = _reservations[ix];
              //console.log ('-----------------');
              if (el.id.toLowerCase() == iinFake.text().toLowerCase()) {
                var rDate = {};
                var data = el.date.split(" ");
                var rDateArr = data[0].split(".");
                console.log("rDateArr " + rDateArr);
                var rTimeArr = data[1].split(":");
                rDate.date = Number(rDateArr[0]);
                rDate.month = Number(rDateArr[1]);
                rDate.hour = Number(rTimeArr[0]);
                rDate.minute = Number(rTimeArr[1]);
                var now = new Date();

                var date_s = new Date();
                date_s.setDate(Number(rDateArr[0]));
                date_s.setMonth(Number(rDateArr[1]) - 1);
                date_s.setHours(Number(rTimeArr[0]));
                date_s.setMinutes(Number(rTimeArr[1]));

                now.setMinutes(now.getMinutes() < 30 ? 0 : 30);

                if (date_s.getTime() == now.getTime()) {
                  $(el.lanes).each(function(ixx) {
                    toggleLaneType(el.lanes[ixx]);
                  });

                } else {
             //   	$('#messageBody').html('<p>Время введенный Вами билета бронирования истек!</p>');
           //       $('#attention_modal').reveal();
                	 //data-reveal-id='attention_modal'
                }
                reservationIsFound = true;
              }
            });

            if (!reservationIsFound) {
            	console.log('reser not ');
              //$('#messageBody').html('<p>Введенный Вами билет бронирования не найден!<br>Убедитесь в правильности кода Вашего билета.</p>');
            	if(firstTime==0){
            		jQuery("body").append(jQuery("<script src='javascripts/jquery.reveal.js'></script"));
                }
                
                if(length < 13){
                    print_iin.fadeOut();
                  }
            	//$('#attention_modal').foundation('reveal', 'open');
            	$('div#attention_modal').reveal();
              //$('#attention_modal').foundation('reveal', 'open');
            }
    	}
    	clearAll();
      }
      function enqueue(language) {
        var cId = iin;
        console.log(cId);
        if (cId == null) {
          cId = "000000000000";
        }
        var arr = lanes.split(";");
        var laneArr = new Array();
        $(arr).each(function(ix) {
          if (arr[ix] != "" && arr[ix].length != 0) {
            laneArr.push(arr[ix]);
          }
        });

        var msg = {
          action: "enqueue",
          lang: language,
          client: cId,
          lanes: laneArr
        };
        console.log('123123');
        console.log(msg);
        XmppClient.send("queue", JSON.stringify(msg));
      }
