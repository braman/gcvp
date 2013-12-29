var _ru = {
	reservation_title: 'Бронировали ли вы очередь?',
	yes: 'Да',
	kiosk_backspace: 'Удалить',
	attention: 'Внимание!',
	lead:'Вашего телефонного номера нету в системе. Проверьте правильно ли введен номер.',
//	kiosk_iin_input:'Введите ваш ИИН',
	kiosk_iin_input:'Введите номер телефона',
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
	lead:'Your telephone number is not present in the system. Check the number.',
//	kiosk_iin_input:'Write your IIN',
	kiosk_iin_input:'Write telephone number',
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
	lead:'Сіздің телефон нөміріңіз базадан табылмады. Нөмеріңіздің нақты дұрыс екенін тексеріңіз',
//	kiosk_iin_input:'ЖСН енгізіңіз',
	kiosk_iin_input:'Телефон нөмірін енгізіңіз',
	ticketCreateDateMessage:'Уақыт',
	laneTypesMessage:'Қызметтер',
	waitingClientsPrintMessage:'Сіздің алдыңыздағы клиент саны: ',
	no: 'Жоқ'
	};
var _bot = false;
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
        /*
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
        */
        printTicket();
    }

      function _initFromLang(data, lang) { //MUST BE CALLED WHEN LANGUAGE BUTTON CLICKED
    	 console.log('lang')
    	  $lanes.empty();
        _data = data;
        _lang = lang;
        if(jQuery.type(data.tree) === 'string'){
        	laneGroup = true;
            var str = data.tree.replace(/&(lt|gt|quot|apos);/g, function (m, p) {
                return (p == 'lt')? '<' : (p == 'gt') ? '>' : (p == 'apos' ? "'" : '"');
            });
            data.tree = JSON.parse(str);
            _laneTree = data.tree;
            console.log (_laneTree);
            mainMenu();
        }else if(jQuery.type(data.tree) === 'array'){
        	laneGroup = true;
        	mainMenu();
        }else{
        	$(_lanes).each(function(ix) {
        		laneGroup = false;
                var el = _lanes[ix];
                if (_lang == 'en-lang') {
                    $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<span>" + el.en + "</span></a></li>");
                } else if (_lang == 'kk-lang') {
                    $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<span>" + el.kk + "</span></a></li>");
                } else {
                    $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "<span>" + el.ru + "</span></a></li>");
                }
        	});
        }
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
//        $('#kiosk-iin-input')[0].placeholder='+7(7__) ___-____';
        var str = '7(7__) ___-____';
        $('#kiosk-iin-input').html(str);
//        $('#kiosk-iin-input').inputmask({ "mask": "+7(799) 999-9999"});
      }
      
	  
      var treeLanes = new Array();
      function mainMenu(){
    	  var counter = 0;
    	  jQuery(_laneTree).each(function(ix){
    		  var laneArr = getMainLane(_laneTree[ix].id).sublanes;
              jQuery(laneArr).each(function(ix){
                  var el = getLane(laneArr[ix]);
                  treeLanes[counter++] = el;
              });
    		  
              console.log ('_laneTree');
              var el = _laneTree[ix];
              if (_lang == 'en-lang') {
            	  $lanes.append("<li><a class='mainLane kiosk-link block' href='#' data-id='"+el.id+"'>" + "<span>" + el.en + "</span></a></li>");
              } else if (_lang == 'kk-lang') {
            	  $lanes.append("<li><a class='mainLane kiosk-link block' href='#' data-id='"+el.id+"'>" + "<span>" + el.kk + "</span></a></li>");
              } else {
                  $lanes.append("<li><a class='mainLane kiosk-link block' href='#' data-id='"+el.id+"'>" + "<span>" + el.ru + "</span></a></li>");
              }
              //jQuery("#lanes").append("<div class='mainLane button' data-id='"+el.id+"'>"+el.ru+"</div>");
          });
    	  if(_lanes.length>treeLanes.length){
    		  $(_lanes).each(function(ix) {
    			    laneGroup = true;
  	      			flag = false;
    	      		var el = _lanes[ix];
    	      		$(treeLanes).each(function(jx) {
    	      			console.log(treeLanes[jx].id +' '+el.id);
        	      		if(treeLanes[jx].id == el.id){
    	      				flag = true;
    	      				return false;
    	      			}
    	      		});
    	      		if(flag == false){
    	      		  if (_lang == 'en-lang') {
      	                  $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "</a></li>");
      	              } else if (_lang == 'kk-lang') {
      	                  $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "</a></li>");
      	              } else {
      	                  $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "</a></li>");
      	              }
    	      		}
    	          });
    	  }
    	  
          jQuery(".mainLane").click(function(){
        	  laneGroup = false;
        	  $lanes.empty();
              console.log ('mainLane');
              console.log (jQuery(this).attr("data-id"));
              console.log (getMainLane(jQuery(this).attr("data-id")));
              var laneArr = getMainLane(jQuery(this).attr("data-id")).sublanes;
              console.log (laneArr);
              step3.fadeOut(300, function(){
                  jQuery(laneArr).each(function(ix){
                      var el = getLane(laneArr[ix]);
                      $lanes.append("<li><a id='" + el.id + "' class='kiosk-link block' href='#' onclick=\" toggleLaneType('" + el.id + "')\">" + "</a></li>");
                  });
      			step3.fadeIn(300);
              });
          });
      //    clearAll();
      }
      function getMainLane(uuid){
          var result = null;
          jQuery(_laneTree).each(function(ix){
              var el = _laneTree[ix];
              if(el.id==uuid){
                  result = el;
              }
          });
          return result;
      }

      
      
      function cleariin(){
    	  print_iin.fadeOut();
    	  $('#kiosk-iin-input').empty();
          var str = '7(7__) ___-____';
    	  $('#kiosk-iin-input').html(str);
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
              step3.fadeIn(300);
            });
         });
          $('#ru-lang').click(function() {
          	  _initFromLang(data, 'ru-lang');
              step1.fadeOut(300, function() {
                step3.fadeIn(300);
              });
            });
          $("#en-lang").click(function() {
        	  _initFromLang(data, 'en-lang');
              step1.fadeOut(300, function() {
                step3.fadeIn(300);
              });
          });
          console.log('head_---------------------------------------');
          jQuery("head").empty();
          document.title = 'Киоск CloudQueue';
          jQuery("head").append(jQuery("<meta charset='utf8'><meta content='width=device-width' name='viewport'><link href='stylesheets/screen.css' media='print,screen' rel='stylesheet'><link href='stylesheets/kiosk.css' media='print,screen' rel='stylesheet'><link href='http://fonts.googleapis.com/css?family=Ubuntu&amp;subset=latin,cyrillic' rel='stylesheet' type='text/css'>"));
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
      
      function bot(){
          _bot = true;
          setInterval(function(){
              var t = parseInt(Math.random()*100000);
              if(t>50000){
            	  setTimeout(function(){
            		  $("#ru-lang").click();
            		  console.log('ru-lang');
            	  },2500);
            	  
            	  setTimeout(function(){
            		  $("#no").click();
            		  console.log('no');
            	  },5000);
    			  setTimeout(function(){
            		  $("#fils01a02").click();
            		  console.log('a')
            	  },7500); 
            	  setTimeout(function(){
            		  $("#fils01a02").click();
            		  console.log('kiosk-print-services')
            	  },10000); 
              }else {
            	  setTimeout(function(){
            		  $("#kk-lang").click();
            		  console.log('ru-lang');
            	  },2500);
            	  
            	  setTimeout(function(){
            		  $("#no").click();
            		  console.log('no');
            	  },5000);
            	  setTimeout(function(){
            		  $("#fils01a02").click();
            		  console.log('a')
            	  },7500); 
            	  setTimeout(function(){
            		  $("#fils01a02").click();
            		  console.log('kiosk-print-services')
            	  },10000);  
              }
          },15000);
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
          jQuery("#ticketCode").text(msg.ticket.code);
          var clientId = msg.ticket.clientIin;
          if (clientId.length == '') {
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
            	  a += getLane(el).en + " (" + waitingClients +")" + ", ";
              }else if(_lang=='kk-lang'){
            	  a += getLane(el).kk + " (" + waitingClients +")" + ", ";
              }else{
            	  a += getLane(el).ru + " (" + waitingClients +")" + ", ";
              }
              //a += getLane(el).ru + " (" + waitingClients;
              //a += "<img src='img/rsz_usestrs.png' width='15px' height='15px'/>";
              //a += ")" + ", "
            }
          });
          //a = a.substr(0, a.length - 2);
          //$("#laneTypes").append(a);
          
          a = a.substr(0,a.length-2);
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
          
          if(_bot == false){
        	  console.log('window');
        	  window.print();
          } 
          console.log('BOT'+ _bot);
          $("#kiosk-step-2").hide();
          $("#kiosk-step-iin").hide();
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
    	console.log('print ticket');
    	var iinText = iinFake.text();
    	var str = iinText.substr(2, 3)+iinText.substr(7, 3)+iinText.substr(11, 4);
		console.log("I AM IN IF");
        if (lanes && lanes.length > 0) {
        	console.log('enqueue print');
        	iin = null;
            enqueue(_lang);
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
