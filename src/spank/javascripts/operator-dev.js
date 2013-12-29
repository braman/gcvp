    var _ru = {
        lane:"Услуга",
        totalwaiting:"Всего ожидают",
        served:"Вы обслужили",
        call:"Вызвать_клиента",
        recall:"Вызвать_отложенного",
        start:"Начать_обслуживание",
        end:"Завершить_обслуживание",
        postpone:"Отложить",
        transfer:"Перенаправить",
        returnToQ:"Отклонить_клиента",
        status:"Статус",
        noclient:"Клиентов нет",
        waiting:"Клиент ожидает",
        called:"Клиент вызван",
        started:"Обслуживание начато",
        ended:"Обслуживание завершено",
        transferred:"Клиент перенаправлен",
        transferredAndBack:"Клиент перенаправлен с возвратом",
        postponed:"Клиент отложен",
        help: "Помощь",
        settings: "Настройки",
        quit: "Выход",
        no_clients: "Нет клиентов",
        declined: "Клиент отклонен",
        back: "Клиент возвращен",
        backError: "Завершите обслуживание",
        choose_lang: "Выберите язык"
    };

    var _kk = {
        lane:"Қызмет",
        totalwaiting:"Күтушілер саны",
        served:"Қызмет көрсетілгендер саны",
        call:"Клиентті шақыру",
        recall:"Тоқтатылған клиентті шақыру",
        start:"Қызметті бастау",
        end:"Қызметті аяқтау",
        postpone:"Уақытша тоқтату",
        transfer:"Бағыттау",
        returnToQ:"Клиент келмеді",
        status:"Жай-күйі",
        noclient:"Клиенттер жоқ",
        waiting:"Клиент күтуде",
        called:"Клиент шақырылды",
        started:"Қызмет көрсетілуде",
        ended:"Қызмет көрсету аяқталды",
        transferred:"Клиент бағытталды",
        postponed:"Клиент уақытша тоқтатылды",
        help: "Көмек",
        settings: "Қондырғылар",
        quit: "Шығу",
        back: "Клиент возвращен",
        backError: "Завершите обслуживание",
        no_clients: "Клиенттер жоқ",
        declined: "Клиент келмеді",
        
        choose_lang: "Тіл таңдаңыз"
    };
    
    var _en = {
            lane:"Service",
            totalwaiting:"Number of waitings",
            served:"You have served",
            call:"Call client",
            recall:"Recall postponed client",
            start:"Start service",
            end:"End service",
            postpone:"Postpone",
            transfer:"Transfer",
            returnToQ:"Decline a client",
            status:"Status",
            noclient:"No clients",
            waiting:"Client is waiting",
            called:"Client was called",
            started:"Service started",
            ended:"Service ended",
            transferred:"Client transferred",
            postponed:"Clients postponed",
            help: "Help",
            settings: "Settings",
            quit: "Quit",
            back: "Клиент возвращен",
            backError: "Завершите обслуживание",
            no_clients: "No clients",
            declined: "Client was declined",
            choose_lang: "Choose language"
        };
    var firstTime = 0;
    var step = '';
    var _token = null;
    var _laneStats = null;
    var _initData = null;
    var _msgBuffer = new Array();
    var _lanes = {};
    var _lang = null;
    var username = null;
    var kk_ru = null;
    var _callBtn = false;
    var _recallBtn = false;
    var _transferred = false;
    var _recalled = false;
    var _any_postponed = false;
    var _count_recall = "";
    var _tokens = null;
    var calledByTicket = '';
    var _choosen_number = "";
    var _msg = "";
  //timer
	var minutesLabel = document.getElementById("minutes");
    var secondsLabel = document.getElementById("seconds");
    var hoursLabel = document.getElementById("hours");
    var totalSeconds = 0;
    var stop = false;
    var start = null;
    var body_text = "";
    
    function _init(data){
    	jQuery("head").empty ();
        //jQuery("head").append(jQuery("css' /><link href='http://fonts.googleapis.com/css?family=Ubuntu:300,400,700&subset=latin,cyrillic' rel='stylesheet' type='text/css'><link rel='stylesheet' href='stylesheets/app.css' /><link rel='stylesheet' href='stylesheets/unit.css' />"));
    	document.title = 'Оператор CloudQueue';
        XmppClient.send("queue","getUsername");
           
        _initData = data;
        location.href = "#!unit";
        jQuery(".buttonContainer .caret").css("margin-left","20px");
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
        	else if(_lang=="en"){
        		kk_ru = _en
        	}
        	
        	jQuery("#help").append(kk_ru.help);
        	jQuery(".settings").append(kk_ru.settings);
        	jQuery("#logoutLink").append(kk_ru.quit);
        	jQuery("#lane").append(kk_ru.lane);
        	jQuery("#totalwaiting").append(kk_ru.totalwaiting);
        	jQuery("#tooltip").append(
        			 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.returnToQ+"><a href='#' id='declineBtn' onclick='declineBtn();'><i class='icon-reply'></i></a></li>" +
					 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.call+"><a href='#' id='callBtn' class='active' onclick='callBtn();'><i class='icon-bullhorn'></i></a></li>" +
					 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.start+"><a href='#' id='startBtn'><i class='icon-play' onclick='startBtn();'></i></a></li>" +
					 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.end+"><a href='#' id='endBtn'><i class='icon-stop' onclick='endBtn();'></i></a></li>" +
					 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.postpone+"><a href='#' id='postponeBtn'><i class='icon-pause' onclick='postponeBtn();'></i></a></li>" +
					 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.recall+"><a href='#' id='recallBtn'><i class='icon-bolt' onclick='recallBtn();'><p id='counter' style='font-size: 11px'></p></i></a></li>" +
        			 "<li data-tooltip class='has-tip tip-bottom' title="+kk_ru.transfer+"><a href='#' id='test'><i class='icon-share-alt' onclick='testBtn();'></i></a></li>");
        }

        if(data.username){
        	jQuery("#username").text(data.username);
        }
        
        if(data.lanes){
            _lanes = data.lanes;
            jQuery(_lanes).each(function(ix){
                var el = _lanes[ix];
                console.log (el);  
            });
            jQuery(_lanes).each(function(ix){
                var el = _lanes[ix];
                jQuery("#transferBtnGroup ul").append("<li><a href='#' onclick='transfer(\""+el.id+"\")'>"+el.ru+"</a></li>");  
            });
            var f=false;
            jQuery(_lanes).each(function(ix){
                var el = _lanes[ix];
                if (!f){
                    jQuery("#lanes").append("<option value='"+el.id+"' selected>"+el.ru+"</option>");
                    f = true;
                }else{
                  //  jQuery("#lanes").append("<button type='button' name='lane' class='btn' value="+ el.id +">" + el.ru + "</button>");
                    jQuery("#lanes").append("<option value='"+el.id+"'>"+el.ru+"</option>");
                //   	jQuery("#lanes").append("<input type='radio' name='lane' value='"+el.id+"'>"+el.ru+"<br>"); 
                }
                                 
            });
        }
        jQuery("#transferBtnGroup ul").append("<li class='divider'></li>");
        if(data.units){
            console.log ('111111111111111111111111111');
            jQuery(data.units).each(function(ix){
                var el = data.units[ix];
                jQuery(data.lanes).each(function(ix){
                    var el = _lanes[ix];
                    console.log (el);  
                });
            });
          //  jQuery("#workers").append("<button type='button' id='workers1' name='lane' class='btn active' value='0'>Любой сотрудник</button>");
            jQuery("#workers").append("<option value='0'>Любой сотрудник</option>");    
            jQuery(data.units).each(function(ix){
                var el = data.units[ix];
            //    jQuery("#workers").append("<button type='button' name='lane' class='btn' value="+ el.unit +">"+ el.name +"</button>"); 
                jQuery("#workers").append("<option value='"+el.unit+"'>"+el.name+"</option>");    
            });
        }

        if(data.postponed){
        	_any_postponed = true;
        	jQuery("#recallBtn").addClass("active");
            jQuery(data.postponed).each(function(ix){
            	_count_recall = _count_recall + 1;
                var el = data.postponed[ix];
               // jQuery("#recallClients").append("<li><a href='#' onclick='recall(\""+el.id+"\")'>"+el.ticket+" ("+el.time+")</a></li>");   
           //     jQuery("#recallClients").append("<button type='button' id='workers1' name='lane' class='btn active' value='"+el.id+"'>"+el.ticket+"</button>");  
                jQuery('#forward-select').append("<option value='"+el.id+"'>"+el.ticket+"\t"+el.lane_text+":</option>");
    			//jQuery("#recallClients").append("<div id='"+el.ticket+"'><input type='radio'  name='client' value='"+el.id+"'>"+el.ticket+"\t"+el.lane_text+"<br/></div>");
            });
        }
        $("#counter").text(_count_recall);
        
        if(data.current){
        	console.log("I am in DATA.CURRENT!!!");
            _token = data.current;
            console.log(_token);
            $(".operator-counter").text(_token.ticket);

            if(_token.started){
            	step = 'started';
            	callBtnFadeIn();
            	start = true;
            	$start.fadeOut(fadeOutTime, function() {
            		   $stop.add($start_counter).fadeIn(fadeOutTime);
            	  })
            	  setTime();
            }else if(_token.called){
            	step = 'called';
            	callBtnFadeIn();
           	}
            jQuery("#token_lane").append(_token.laneText);
        	jQuery("#info_text").empty();
        	jQuery("#info_text").append(kk_ru.called);
        	
        	_callBtn = false;
        	
            jQuery(".tokenId").text("Token id (for debug):"+_token.id);
        }
        /*
        $('#customDropdown1').change(function() {
        	jQuery(this).removeAttr("selected");
            var selected = $(this).find(":selected").text();
        	selectedGroup = jQuery('#customDropdown1').val();
    		console.log(selectedGroup);
        	goToFilial();
        	});
        */
        jQuery("#lanes").change(function() {
            $('#workers').empty();
            var lane1 = $(this).find(":selected").val()
            jQuery("#workers").append("<option value='0'>Любой сотрудник</option>");    
            jQuery(data.unit_lanes).each(function(ix){
                var el = data.unit_lanes[ix];
                if (el.lane == lane1){
                    jQuery("#workers").append("<option value='"+el.unit+"'>"+el.name+"</option>");    
                } 
            });
        });
        
        XmppClient.send("queue",JSON.stringify({action:"statistics"}));
    }

	function getSettings(){
		jQuery('#messageBody').html(
				'<p>'+kk_ru.choose_lang+'</p>' +
				'<label for="ru_lang"><input type="radio" id="ru_lang" name="lang" value="ru" />Русский</label>' +
				'<label for="kk_lang"><input type="radio" id="kk_lang" name="lang" value="kk" />Казахский</label>' +
				'<label for="en_lang"><input type="radio" id="en_lang" name="lang" value="en" />English</label>');
		$('#message').foundation('reveal','open');
	}
    function bot(){
        _bot = true;
        setInterval(function(){
        	setTimeout(function(){
        		if(step == ''){
        			callBtn();
            	}
        		console.log('ru-lang');
        	},2500);
        	
        	setTimeout(function(){
        		if(step == 'called'){
        			startBtn();
        			step = 'started';
        		}
        		console.log('ru-lang');
        	},7500);
        	var t = parseInt(Math.random()*100000);
        	if(t>50000){
        		setTimeout(function(){
            		anketaBtn();
            		console.log('ru-lang');
            	},10000);
            }
        	setTimeout(function(){
        		if(step == 'started'){
        			endBtn();
            		step = '';
        		}
        		console.log('ru-lang');
        	},17500);        
        },20000);
    }
    
	function callBtn()
	{
		console.log('call btn clicked');
		if(_callBtn == false){
    		XmppClient.send("queue","call");	
    		_callBtn = true;
    		step = 'called';
        	_recalled = false;        	
    	}        
    }

	function anketaBtn()
	{
		alertify.log("Анкета показана клиенту")
    	console.log('anketa btn clicked');
    	XmppClient.send("queue","anketa");
    }
	
	function showNumbersBtn(){
		alertify.log("Номера показаны клиенту");
    	console.log('showNumbers btn clicked');
    	XmppClient.send("queue","numbers");
	}
	
	function startBtn(){
		jQuery("#info_text").empty();
    	jQuery("#info_text").append(kk_ru.started); 
    	console.log(_ru.called);
   		stop = false;
   		step = 'started';
   		start = true;
    	var token_id = $(".operator-counter").text();
    	$("#"+token_id).remove();
    	XmppClient.send("queue","start");        
	    alertify.success(kk_ru.started)
	    setTime();
    	$start.fadeOut(fadeOutTime, function() {
  	      $stop.add($start_counter).fadeIn(fadeOutTime);
  	    })
    }
	
	function setTime()
    {
        ++totalSeconds;
        secondsLabel.innerHTML = pad(totalSeconds%60);
        minutesLabel.innerHTML = pad(parseInt(totalSeconds%3600/60));
        hoursLabel.innerHTML = pad(parseInt(totalSeconds/3600));
        if(!stop){
        	setTimeout(setTime, 1000);
        }else{
           	totalSeconds = 0;
        }
    }
	
	function pad(val)
    {
        var valString = val + "";
        if(valString.length < 2)
        {
            return "0" + valString;
        }
        else
        {
            return valString;
        }
    }
	
	function endBtn(){
		if(calledByTicket != ''){
			_tokens = removeFunction(_tokens,"id",calledByTicket);
			calledByTicket = '';
		}
	  	if(_any_postponed == true)
    		jQuery("#recallBtn").addClass("active");
    	
    	stop = true;
    	start = false;
    	step = '';
    	$("#choosen_number").empty();
    	XmppClient.send("queue","end");
    	operator_steps.back_to_step_1_from_step_2();
    	if ($start_counter.is(":visible")) {
		   $stop.add($start_counter).fadeOut(fadeOutTime, function() {
		    $start.fadeIn(fadeOutTime);
		    //$operator_step_1.fadeIn();
		   });
		  } else {
		   $stop.fadeOut(fadeOutTime, function() {
		    $start.fadeIn(fadeOutTime);
		    //$operator_step_1.fadeIn();
		   });
		 }
    	clearCounter();
    	alertify.success(kk_ru.ended)    	
	}
	
	function declineBtn(){
		clearCounter();
		jQuery("#token_lane").empty();
    	jQuery("#token_lane").append(_token.laneText);
    	operator_steps.back_to_step_1_from_step_2();
    	$("#choosen_number").empty();
    	XmppClient.send("queue",JSON.stringify({action:"decline",token_id:_token.id}));
        alertify.success(kk_ru.declined);        
    }
	function backBtn(){
		if(start==null || start == false){
			clearCounter();
			jQuery("#token_lane").empty();
	    	jQuery("#token_lane").append(_token.laneText);    	
	    	operator_steps.back_to_step_1_from_step_2();
	    	XmppClient.send("queue",JSON.stringify({action:"back",token_id:_token.id}));
	        alertify.success(kk_ru.back);	        
        }else{
        	alertify.error(kk_ru.backError);
        }	
    }
	function postponeBtn(){
    	clearCounter();
    	jQuery("#info_text").empty();
    	
    	jQuery("#info_text").append(kk_ru.postponed);
        
    	stop = true;
    	start = false;
    	operator_steps.back_to_step_1_from_step_2();
    	$("#choosen_number").empty();
    	XmppClient.send("queue","postpone");    	
	}
	function recallClient(){
		var forward_select = $('#forward-select');
		var client = forward_select.val();
		var checked = forward_select.find(":selected").text();
		console.log("Client:" + client);
        if(client!=null){
        	$("#forward-select option[value='"+client+"']").remove();
        	
        	recall(client);
        	_recallBtn = true;
        	
        	console.log(checked);
        	if(firstTime==0){
        		firstTime++;
        		jQuery("body").append(jQuery("<script src='javascripts/jquery.reveal-ck.js'></script"));
            }
        	$(".operator-counter").text(checked.substring(0,3));
        	$('#callpostponed_modal').trigger('reveal:close');
        	
            callBtnFadeIn();
        }else{
        	$('#callpostponed_modal').trigger('reveal:close');
        }
        $("#choosen_number").empty();
        //$('#callpostponed_modal').reveal();
        return false;
    }
	 function showTokensModal(id){
		 if(step == ''){
			 console.log(id);
			 jQuery('#token-select').empty();
			 jQuery(_tokens).each(function(ix){
	     		var el = _tokens[ix];
	     		if(el.laneId == id){
	     			jQuery('#token-select').append("<option value='"+el.id+"'>"+el.ticket+"</option>");
	     	    }
	     	});
			 if(firstTime==0){
					firstTime++;
		 		jQuery("body").append(jQuery("<script src='javascripts/jquery.reveal.js'></script"));
		     }
			 $('#call_token_modal').reveal();
		 }
	}
	 
	function callClientByTicket(){
		var forward_select = $('#token-select');
		var client = forward_select.val();
		var checked = forward_select.find(":selected").text();
		console.log("Client:" + client);
		if(_callBtn == false && client!=null){
    		_callBtn = true;
        	_recalled = false;        	
        	$("#token-select option[value='"+client+"']").remove();
        	console.log(checked);
        	$('#call_token_modal').trigger('reveal:close');
        	calledByTicket = client;
        	XmppClient.send("queue",JSON.stringify({action:"callByTicket",ticketTokenId:client}));        	        
        }   
        
        //$('#callpostponed_modal').reveal();
        return false;
    }
	
	function testBtn(){
		console.log("#forward was clicked");
		if(firstTime==0){
			firstTime++;
    		jQuery("body").append(jQuery("<script src='javascripts/jquery.reveal-ck.js'></script"));
        }
        $('#forward_modal').reveal();
		  $reveal_forward_links.show();
		  $("#choosen_number").empty();
		return false;  
	}
	
	function backToUnit() {
		$("#body_settings").hide();
		$("#body").show();
	}
	
	function readBlob(opt_startByte, opt_stopByte) {
	    var files = document.getElementById('files').files;
	    if (!files.length) {
	      return;
	    }

	    var file = files[0];
	    var start = parseInt(opt_startByte) || 0;
	    var stop = parseInt(opt_stopByte) || file.size - 1;

	    var reader = new FileReader();

	    reader.onloadend = function(evt) {
	      if (evt.target.readyState == FileReader.DONE) { 
	        document.getElementById('byte_content').textContent = evt.target.result;
	        XmppClient.send("queue",JSON.stringify({action:"parseNumbers",numbers:jQuery('#byte_content').text()}));
	      }
	    };
	    
	    var blob = file.slice(start, stop + 1);
	    reader.readAsBinaryString(blob);
	  }
	
	function settingsBtn(){
		/*var new_body = '<input type="file" id="files" name="files[]" multiple />'
			+'	<output id="list"></output>'

			+'<script>'
			+'  function handleFileSelect(evt) {'
				  +' var files = evt.target.files;'

			    +' var output = [];'
			    +'for (var i = 0, f; f = files[i]; i++) {'
			    +"  output.push('<li><strong>', escape(f.name), '</strong> (', f.type || 'n/a', ') - ',"
			            +"      f.size, ' bytes, last modified: ',"
			          +"        f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a',"
			        +"          '</li>');"
			    +'}'
			    +"document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';"
			  +'}'

			  +"document.getElementById('files').addEventListener('change', handleFileSelect, false);"
			+'</script>'*/
		
		var new_body = '<style>'
			+'#byte_content {'
			+'  margin: 5px 0;'
		+'  max-height: 100px;'
		+'  overflow-y: auto;'
		+'  overflow-x: hidden;'
		+'}'
		+'#byte_range { margin-top: 5px; }'
		  +'</style>'
		+'<a href="#" onclick="backToUnit()">Back</a>'
		+'<input type="file" id="files" name="file" /> Read bytes: '
			+'<span class="readBytesButtons">'
		  +'<button id="fileToString" onclick="readBlob(null, null)">entire file</button>'
		  +'</span>'
		+'<div id="byte_content"></div>';
/*
		+'<script>'
		+'function readBlob(opt_startByte, opt_stopByte) {'

		+"var files = document.getElementById('files').files;"
		    +'if (!files.length) {'
		    +"alert('Please select a file!');"
		      +'return;'
		      +'}'

		    +'var file = files[0];'
		    +'var start = parseInt(opt_startByte) || 0;'
		    +'var stop = parseInt(opt_stopByte) || file.size - 1;'

		    +'var reader = new FileReader();'

		    +'reader.onloadend = function(evt) {'
		    	+'if (evt.target.readyState == FileReader.DONE) { '
		    	+"document.getElementById('byte_content').textContent = evt.target.result;"
		      +"document.getElementById('byte_range').textContent = "
		        	+"['Read bytes: ', start + 1, ' - ', stop + 1,"
		             +"       ' of ', file.size, ' byte file'].join('');"
		        +'}'
		      +'};'

		    +'var blob = file.slice(start, stop + 1);'
		    +'reader.readAsBinaryString(blob);'
		    +'}'
		  
		    +"document.querySelector('.readBytesButtons').addEventListener('click', function(evt) {"
			  +"if (evt.target.tagName.toLowerCase() == 'button') {"
		    	+"var startByte = evt.target.getAttribute('data-startbyte');"
		      +"var endByte = evt.target.getAttribute('data-endbyte');"
		      +'readBlob(startByte, endByte);'
		      +'}'
		    +'  }, false);'
		+'</script>';*/
		$("#body").hide();
		$("#body_settings").show();
		if($("#body_settings").html().length == 0){
			$("#body_settings").append(new_body);
		}
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
    		kk_ru = _en;
    	}
        
        jQuery(_laneStats).each(function(ix){
            var el = _laneStats[ix];
            var lane_text = null;
            var laneName = getLane(el.lane);
            if(laneName!=null){
            	if(_lang=="ru"){
                	lane_text = laneName.ru;
                }else if(_lang=="kk"){
                	lane_text = laneName.kk;
                }else if(_lang=="en"){
                	lane_text = laneName.en;
            	}	
                jQuery(".operator-services-list").append("<p onclick = 'showTokensModal();' class='operator-services-list-name'>"+lane_text+"<span class='operator-services-list-item'>"+el.waiting+"</span></p>");  
                
            }
        });
        
        console.log("LANGUAGE IS: " + _lang);
        $('#message').foundation('reveal','close');
		XmppClient.send("queue", JSON.stringify({action:"changeLang", user:sessionStorage.jid, language:_lang}));
		//locale(_lang);
	}
    
    function recall(tokenId){
        XmppClient.send("queue",JSON.stringify({action:"recall",token:tokenId}));
        return false;
    }

    function getLane(lane){
        var result = null;
        jQuery(_lanes).each(function(ix){
            if(_lanes[ix].id==lane){
                result = _lanes[ix];
            }
        });
        return result;
    }
    var rButton = 1;
    function transferTest(){  
    	var lane1 = jQuery('#lanes').val();
    	var lane2 = jQuery('#workers').val();
    	rButton = jQuery('#position').val();
    	//var lane1 = $("input:radio[name ='lane']:checked").val();
        console.log ("Lane1: " + lane1);
        //var lane2 = $("input:radio[name ='worker']:checked").val();
        console.log ("Lane2: " + lane2);
/*
        jQuery("#lanes .active").removeClass("active");
        jQuery("#workers .active").removeClass("active");
        jQuery("#btn-group-vertical1").addClass("active");
        jQuery("#workers1").addClass("active");
        rButton = $("input:radio[name ='radio']:checked").val();
        */
        //console.log (a);
        
        console.log("Token: "+_token);
        if(_token.ticket!=null){
        	_transferred = true;
        	console.log("I am in _token");
            
            jQuery("#info_text").empty();
            jQuery("#info_text").append(kk_ru.transferred);
            stop = true;
            
            //var token_id = $(".operator-counter").text();
        	//$("#"+token_id).remove();
            
        	console.log("============Transferred: "+kk_ru.transferred);
            if (lane2==0){
                console.log (lane1);
                transfer(null,lane1,false);   
            }else{
                console.log (lane2);
                transfer(lane1,lane2,false);
            }
			
        }
        $('#forward_modal').trigger('reveal:close');
        //$('#laneModal').foundation('reveal','close');
        clearCounter();
        jQuery("#info_text").empty();
        jQuery("#info_text").append(_ru.transfered);
        alertify.success(kk_ru.transferred);
        operator_steps.back_to_step_1_from_step_2();
        return false;
    }
    function transferReturnTest(){
    	var lane1 = jQuery('#lanes').val();
    	var lane2 = jQuery('#workers').val();
    	rButton = jQuery('#position').val();
    	//rButton = $("input:radio[name ='radio']:checked").val();
        //console.log (a);
        //var lane1 = $("input:radio[name ='lane']:checked").val();
        console.log (lane1);
        //var lane2 =  $("input:radio[name ='worker']:checked").val();
        
        
        console.log (lane2);
        if(_token.ticket!=null){
        	_transferred = true;
        	console.log("I am in _token");
        	if(_any_postponed == true)
        		jQuery("#recallBtn").addClass("active");
            
            jQuery("#info_text").empty();
            jQuery("#info_text").append(kk_ru.transferred);
            stop = true;
        	/*
            var token_id = $(".operator-counter").text();
        	$("#"+token_id).remove();
            */
            if (lane2==0){
                console.log (lane1);
                transfer(null,lane1,true);   
            }else{
                console.log (lane2);
                transfer(lane1,lane2,true);
            }
        }
        clearCounter();
        $('#forward_modal').trigger('reveal:close');
        //$('#laneModal').foundation('reveal','close');
        alertify.success(kk_ru.transferredAndBack);
        operator_steps.back_to_step_1_from_step_2();
        return false;
    }    
    function transfer(transferredLane,target,rToken){
        console.log(target);
        console.log(_token.id);
        console.log (rButton+" radioButton");
        XmppClient.send("queue",JSON.stringify({action:"transfer",token:_token.id,laneName:transferredLane,destination:target,radioButton:rButton,returnToken:rToken}));
        return false;
    }

    function _onMessage(text){
        var msg = {};
        try{
            msg = JSON.parse(text); 
        }catch(e){
            console.warn("unit got non-JSON message:"+text);
            return;
        }
        console.log(msg);
        if(_laneStats){
            processMessage(msg);
        }
        else{
            if(msg.stats){
                _laneStats = msg.stats;
                
                jQuery(_laneStats).each(function(ix){
                    var el = _laneStats[ix];
                    var lane_text = null;
                    
                    var laneName = getLane(el.lane);
                    if(laneName!=null){
                    	if(_lang=="ru"){
                        	lane_text = laneName.ru;
                        }else if(_lang=="kk"){
                        	lane_text = laneName.kk;
                        }else if(_lang=="en"){
                        	lane_text = laneName.en;
                    	}	
                    	jQuery(".operator-services-list").append("<p onclick=\"showTokensModal('"+ el.lane +"\');\" class='operator-services-list-name'>"+lane_text+"<span class='operator-services-list-item' id='"+ el.lane +"'>"+(el.waiting+el.postponed+el.transferred)+"</span></p>");  
                    }
                	//jQuery(".statistics tbody").append("<tr data-lane='"+el.lane+"'><td></td><td></td></tr>")
                });
                jQuery(_msgBuffer).each(function(ix){
                    processMessage(_msgBuffer[ix]);
                });
            }if(msg.tokens){
            	_tokens = msg.tokens;
            }else{
                _msgBuffer.push(msg);
            }
        }
    }
    
    function removeFunction (myObjects,prop,valu)
    {
         return myObjects.filter(function (val) {
          return val[prop] != valu;
      });

    }
    function processMessage(msg){
        console.log("MSG.PUBSUB is " + msg.pubsub);
        console.log(msg);
        if(msg.pubsub){
            console.log("I am in MSG.PUBSUB");
            console.log(msg.postpone);
            if(!msg.postpone){
        		console.log('not postpone');
            }
            if(_recalled == false){
            	
            	if(msg.newticket){
            		var counter = $('#'+msg.newticket.lane);
                	//var counter = jQuery("tr[data-lane='"+msg.newticket.lane+"'] td:eq(1)");
                	counter.text(parseInt(counter.text())+1);
            	}else if(msg.called){
            		console.log('-------------'+msg.lane);
            		if(!msg.postpone){
            			var counter = $('#'+msg.lane);
            			console.log(counter.text());
            			console.log(parseInt(counter.text()));
            			if(parseInt(counter.text())>0){
            				counter.text(parseInt(counter.text())-1);
            			}            			
                   	}
                	//var counter = jQuery("tr[data-lane='"+msg.lane+"'] td:eq(1)");
                }else if(msg.transferred){
                	//var counter = jQuery("tr[data-lane='"+msg.nextLane+"'] td:eq(1)");
                	var counter = $('#'+msg.lane);
                	counter.text(parseInt(counter.text())+1);
            	}else if(msg.waiting){
            		console.log($('#g').text());
                    console.log($('#b').text());
                    console.log($('#a').text());
            		console.log('waiting');
                	//var counter = jQuery("tr[data-lane='"+msg.nextLane+"'] td:eq(1)");
                	var counter = $('#'+msg.lane);
                	counter.text(parseInt(counter.text())+1);
            	}
            }
        }else
        if(msg.token){
        	console.log("I am in MSG.TOKEN");
        	 
            _token = msg.token;
           
            jQuery("#token_lane").empty();
            jQuery("#token_lane").append(_token.laneText);
            
            jQuery(".tokenId").text("Token id (for debug):"+_token.id);
            
            if(_callBtn == true){
            	$(".operator-counter").text(_token.ticket)
            	alertify.success("Клиент вызван");
            	callBtnFadeIn();
            	step = 'called';
//            	var counter = $('#'+_token.lane);
//            	if(parseInt(counter.text())>0){
//            		counter.text(parseInt(counter.text())-1);
//                }
            	_callBtn = false;
            }
            else if(jQuery("#recallBtn").hasClass("active") && _recallBtn == true){
            	_recallBtn = false;
            	
				_count_recall = _count_recall - 1;
            	
            	jQuery("#counter").text(_count_recall);
            	
				jQuery("#info_text").empty();
            	jQuery("#info_text").append(kk_ru.called);
            }
            else if(_transferred == true){
            	jQuery("#info_text").empty();
                jQuery("#info_text").append(kk_ru.transferred);
                _transferred = false;
            }
            
          //  jQuery("#recallBtnGroup ul a[onclick='recall(\""+_token.id+"\")']").remove();
            if(_token.status=="postponed"){
            	var counter = $('#'+_token.lane);
            	counter.text(parseInt(counter.text())+1);
        	
            	_any_postponed = true;

            	alertify.success("Клиент отложен");

              //  jQuery("#recallBtnGroup ul").append("<li><a href='#' onclick='recall(\""+_token.id+"\")'>"+_token.ticket+" ("+new Date().toTimeString().split(" ")[0]+")</a></li>");
            	jQuery('#forward-select').append("<option value='"+_token.id+"'>"+_token.ticket+"\t"+_token.laneText+":</option>");
            	//jQuery("#recallClients").append("<div id='"+_token.ticket+"'><input type='radio' name='client' value='"+_token.id+"'>"+_token.ticket+"\t"+_token.laneText+"<br/></div>");
            }else if(_token.status=="recalled"){
            	var counter = $('#'+_token.lane);
            	if(parseInt(counter.text())>0){
            		counter.text(parseInt(counter.text())-1);
                }
            
            	_any_postponed = true;

            	alertify.success("Клиент вызван");

              //  jQuery("#recallBtnGroup ul").append("<li><a href='#' onclick='recall(\""+_token.id+"\")'>"+_token.ticket+" ("+new Date().toTimeString().split(" ")[0]+")</a></li>");
            	//jQuery('#forward-select').append("<option value='"+_token.id+"'>"+_token.ticket+"\t"+_token.laneText+":</option>");
            	//jQuery("#recallClients").append("<div id='"+_token.ticket+"'><input type='radio' name='client' value='"+_token.id+"'>"+_token.ticket+"\t"+_token.laneText+"<br/></div>");
            }else{
            	if(jQuery("#recallClients").is(":empty")){
            		_any_postponed = false;
            	}
            	console.log("Checking if empty-------------------------");
            }
            
        }else if(msg.firstname){
        	console.log("I am in MSG.FIRSTNAME");
        	username = msg.firstname;
            jQuery("#username").text(username);
            console.log("=================================Username: " + username);
        }else if(msg.number) {
        	_msg = msg;
        	_choosen_number = msg.content.number;
        	alertify.success("Клиент выбрал номер: " + _choosen_number);
        	$("#choosen_number").empty();
        	$("#choosen_number").append("<span>Клиент выбрал номер: " + _choosen_number + "</span>");
        } else{
    		jQuery("#recallBtn").removeClass("active");
        	console.log("I am in ELSE");
            _token = {};
            if(_callBtn){
            	alertify.error("Клиент не может быть вызван");
            }
            _callBtn = false;
            
            if(jQuery("#declineBtn").hasClass("active")){
            	jQuery("#declineBtn").removeClass("active");
            	jQuery("#callBtn").addClass("active");
            	if(_any_postponed == true)
            		jQuery("#recallBtn").addClass("active");
            	
            	jQuery("#startBtn").removeClass("active");
            	jQuery("#info_text").empty();
            	$("#choosen_number").empty();
            	jQuery("#info_text").append(kk_ru.declined);
            }
            else{
            	jQuery("#callBtn").addClass("active");
            	jQuery("#token_lane").empty();
            	jQuery("#token_lane").append("---");
            	jQuery("#info_text").empty();
            	jQuery("#info_text").append(kk_ru.no_clients);
            }
        }
        console.log('pubsub finished');
        console.log($('#g').text());
        console.log($('#b').text());
        console.log($('#a').text());
    }
    
    function clearCounter(){
    	$(".operator-counter").empty();
    }
    
    function _onStatus(status){
        if(status=="DISCONNECTED"){
        	console.error("Disconnected");
        }
    }
