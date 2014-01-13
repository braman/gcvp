  var language_choose_title = $(".language-chooser-title"),
    reminders = ["Тілді таңдаңыз", "Выберите язык"],
    i = 0;
  var _interval = null;
  var laneGroup = false;
  $(document).ready(function() {
	  console.log('document ready');
	  _interval = setInterval(function() {
	    i++;
	    language_choose_title.fadeOut(function() {
	      $(this).text(reminders[(i % 2)]);
	    }).fadeIn();
	    if (i % 2 === 0) {
	      i = 0;
	    }
	  }, 3000);
  });
  var step1 = $("#kiosk-step-1"),
    step3 = $("#kiosk-step-3"),
    services = $("#services"),
    kiosk_services = $("#kiosk-services a"),
    print_iin = $("#kiosk-print-iin"),
    backtostep1 = $("#back-to-step-1");
    
  backtostep1.on('click', function(){
	if(!laneGroup){
   	  if(_data.tree){
		laneGroup = true;
	  }
	  $lanes.empty();
	  step3.fadeOut(300, function(){
		  	mainMenu();
			step1.fadeIn(300);
		});	
	}else{
		step3.fadeOut(300, function(){
			step1.fadeIn(300);
		});	
	}
  });


  String.prototype.replaceAt=function(index, character) {
      return this.substr(0, index) + character + this.substr(index+character.length);
   }
  

var _ru = {
	ticketCreateDateMessage:'Время:',
	laneTypesMessage:'Услуга:',
	waitingClientsPrintMessage:'Количество клиентов перед Вами: '
};
var _kk = {
	ticketCreateDateMessage:'Уақыт',
	laneTypesMessage:'Қызмет',
	waitingClientsPrintMessage:'Сіздің алдыңыздағы клиент саны: '
};
lanes = "",
$lanes = $("#kiosk-services"),
_lanes = null,
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
	    var splittedLanes = lanes.split(";");
        var counter = 0;
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
        	location.reload();
        }, 120000);
        
        if(_lang=="kk-lang"){
    		kk_ru = _kk;
    	}else if(_lang=="ru-lang"){
    		kk_ru = _ru;
    	}else {
    		kk_ru = _ru;
    	}
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

      
      function clearAll(){
    	  lanes = "";
      }
      
      function _init(data) {
    	  console.log('init');
    	_data = data;
        if (data.lanes) {
          _lanes = data.lanes;
          console.log(_lanes);
        }
        if (data.group) {
          _group = data.group;
        }
        
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
          console.log('head_---------------------------------------');
          jQuery("head").empty();
          document.title = 'Киоск ГЦВП';
          jQuery("head").append(jQuery("<meta charset='utf8'><meta content='width=device-width' name='viewport'>"));
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
          jQuery("#ticketCode").text(msg.ticket.code);
          $("#ticketCreateDateMessage").text(kk_ru.ticketCreateDateMessage);
          var date = new Date();
          $("#ticketCreateDate").text(date.toJSON().split("T")[0] + " " + date.toTimeString().split(" ")[0]);
          $("#laneTypesMessage").text(kk_ru.laneTypesMessage);

          var a = "";

          $(msg.ticket.lanes).each(function(ix) {
            if (msg.ticket.lanes[ix].length > 0) {
              var el = msg.ticket.lanes[ix]
              if(_lang=='kk-lang'){
            	  a += getLane(el).kk;
              }else{
            	  a += getLane(el).ru;
              }
            }
          });
          console.log(a);
          $("#laneTypes").text(a+'');
          var waitingClients = 0;

          $(msg.ticket.lanes).each(function(ix) {
            var b = msg.ticket.lanes[ix];

            $(_lanes).each(function(jx) {
              var el = _lanes[jx];
              if (el.id == b) {
                waitingClients = waitingClients + el.count - 1;
              }
            });
          });

          $("#waitingClientsPrintMessage").text(kk_ru.waitingClientsPrintMessage + waitingClients);
          
          window.print();
          
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
                _lanes[jx] = el;
              }
            });
          });

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
    	if (lanes && lanes.length > 0) {
        	enqueue(_lang);
        }
       	clearAll();
      }
      function enqueue(language) {
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
          client: "000000000000",
          lanes: laneArr
        };
        console.log('send message to other about new ticket');
        console.log(msg);
        XmppClient.send("queue", JSON.stringify(msg));
      }
