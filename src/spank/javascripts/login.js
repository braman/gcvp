	var templateLoaded = false;
	var _group = "";
	var _bot = false;
	var _locale = "ru";
	var _template = "";
	var _containerPanel = "";
	var group = null;
	//var myHost = 'localhost:9090';
	var myHost = 'cq.b2e.kz';
	var localHost = 'localhost:9090';
	var mili = 0;
	var _datadata = null;
    
    function loginBtnClick(){
    	var login = jQuery("#loginField").val();
    	login = login.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    	var password = jQuery("#passwordField").val();
        console.log('loginBtn');
	    XmppClient.connect(login,password);
        return false;
    }
    
	jQuery(document).ready(function(){
		if(sessionStorage.getItem('xmpp-jid')!=null){
			XmppClient.connect(sessionStorage.getItem('xmpp-jid'),sessionStorage.getItem('xmpp-pwd'));
		}
      $("#main li a").click(function(event){   
          event.preventDefault();
          $('html,body').animate({scrollTop:$(this.hash).offset().top - 30}, 500);
        });
        $("#cloudqueue-menu li a,#company-menu li a,#buying-menu li a,#support-menu li a").click(function(event){   
          event.preventDefault();
          $('html,body').animate({scrollTop:$(this.hash).offset().top - 110}, 500);
        });
        /*
        $("#login_a").click(function(){
            console.log('login_a');
          $("#login_modal").removeClass("bounceOut");
          $("#login_modal").show();
          $("#login_modal").addClass("bounceIn");
        });
        $("#close_a").click(function(){
          $("#login_modal").removeClass("bounceIn");
          $("#login_modal").addClass("bounceOut");
          $("#login_modal").delay(1000).hide(0);
        });
		*/

//        jQuery("#loginBtn").click(function(e){
//        	var login = jQuery("#loginField").val();
//        	login = login.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
//        	var password = jQuery("#passwordField").val();
//            console.log('loginBtn');
//    	    XmppClient.connect(login,password);
//            return false;
//        });

        jQuery("#passwordField").keyup(function(e){
            if(e.which==13){
                jQuery("#loginBtn").click();
            }
        });
        
    	var QueryString = function () {
  		  // This function is anonymous, is executed immediately and 
  		  // the return value is assigned to QueryString!
  		  var query_string = {};
  		  var query = window.location.search.substring(1);
  		  var vars = query.split("&");
  		  for (var i=0;i<vars.length;i++) {
  		    var pair = vars[i].split("=");
  		    	// If first entry with this name
  		    if (typeof query_string[pair[0]] === "undefined") {
  		      query_string[pair[0]] = pair[1];
  		    	// If second entry with this name
  		    } else if (typeof query_string[pair[0]] === "string") {
  		      var arr = [ query_string[pair[0]], pair[1] ];
  		      query_string[pair[0]] = arr;
  		    	// If third or later entry with this name
  		    } else {
  		      query_string[pair[0]].push(pair[1]);
  		    }
  		  } 
  		    return query_string;
  		} ();
  		var username = QueryString.u;
  		var pwd = QueryString.p;
  		$('#loginField').val(username);
  		$('#passwordField').val(pwd);
  		console.log(username);
  		if(username!=undefined){
  			loginBtnClick();
  		}
  		
	});
    
    _containerPanel = jQuery("#body").html();
    //XmppClient
    function onMessage(text){
        console.log("has _onMessage:"+(typeof _onMessage=="function"));
        if(templateLoaded && typeof _onMessage=="function"){
            _onMessage(text);
        }else{
            //console.log("onMessage:"+text);
            var msg = {}; 
            try{
                msg = JSON.parse(text);
            }catch(e){
                console.warn("Got non-JSON:"+text);
            }
            console.log(msg);
            if(msg.template){
                _group = msg.group;
                var html = msg.template.replace(/&(lt|gt|quot|apos);/g, function (m, p) { 
                    return (p == 'lt')? '<' : (p == 'gt') ? '>' : (p == 'apos' ? "'" : '"');
                });
                _template = html;
                jQuery("#body").html(html);
                var translated = Mustache.render(_template, _ru);
                jQuery("#body").html(translated);
                if(typeof _init=="function"){
                    _init(msg);
                }
                jQuery("#body").show();
                templateLoaded = true;

                console.log('template');
             }else if(msg.url){
                _group = msg.group;

                var html = "";
                
                jQuery("#body").load(msg.url,function(html){
				    console.log(msg.url);
                    //_template = html;
                    //var translated = Mustache.render(_template, _ru);
                    //jQuery("#body").html(translated);
//                    console.log(msg);
                    
                    if(typeof _init=="function"){
                        console.log('_initStarted');
                        _init(msg);
                    }
                    jQuery("#body").show();
                });
                console.log('url');
                templateLoaded = true;
            }
        }
    }

    //XmppClient 
    function onStatus(status){
        console.log(ruStatuses[status]);
		if(status=="AUTHFAIL"){
			jQuery("#login_error").text('Логин или пароль не верны.');
        }else{
    		jQuery("#login_error").text('');
        }
        if(status=="CONNECTED" || status=="ATTACHED"){
            jQuery("#loginPanel").hide();
            XmppClient.send("queue","init");
            jQuery("#userPanel .username").text(sessionStorage.jid.replace("@cq.b2e.kz",""));
            jQuery("#userPanel").show();
        }
        if(status=="DISCONNECTED"){
            jQuery("#containerPanel").empty();
            templateLoaded = false;
        }
        if(status=="SIDEXPIRED"){
            jQuery("#containerPanel").html(_containerPanel);
            templateLoaded = false;
        }

		if(templateLoaded && typeof _onStatus=="function"){
			_onStatus(status);
		}
	}
