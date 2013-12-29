/*
 * Basic client for XMPP using strophe.js

 * Dependencies: jquery.js, strophe.js
 * You must implement onStatus(status) and onMessage(msg) functions on your page
 * */
var ruStatuses = {
	ERROR: "Ошибка",
	CONNECTING: "Подключение...",
	CONNFAIL: "Ошибка подключения",
	AUTHENTICATING: "Авторизация...",
	AUTHFAIL: "Ошибка авторизации",
	CONNECTED: "Подключено",
	DISCONNECTING: "Разъединение...",
	DISCONNECTED: "Разъединено",
	ATTACHED: "Подключено.",
	SIDEXPIRED: "SID заэкспайрился..."
};


var XmppClient = {
    // Alisher
	login: null,
	pass: null,
	// Alisher
		
	connection: null,
	serviceUrl: "/http-bind/",
	domain: "cq.b2e.kz",
	
	jid: null,
	xmppTimer:null,
    isConnected:false,

    log: function (msg) {
        jQuery("#log").append("<p>" + msg + "</p>");
    },
	
    message: function (message) {
    	console.log(message);
		var body = jQuery(message).find("html > body");
		if (body.length === 0) {
			body = jQuery(message).find('body');
			if (body.length > 0) {
				body = body.text();
			}else {
				body = null;
			}
		}else {
			body = body.contents();
			var span = jQuery("<span></span>");
			body.each(function () {
				if (document.importNode) {
					jQuery(document.importNode(this, true)).appendTo(span);
				}else {
					// IE workaround
					span.append(this.xml);
				}
			});
			body = span;
		}
		if (body) {
			onMessage(body);
		}
		return true;
    },
    
    callback: function (status, error) {
		if (status === Strophe.Status.ERROR) {
			onStatus("ERROR", error);
		} else if (status === Strophe.Status.CONNECTING) {
			onStatus("CONNECTING");
		} else if (status === Strophe.Status.CONNFAIL) {
			onStatus("CONNFAIL");
			if(error){
				console.log("[XmppClient] error:"+error);	
			}
		} else if (status === Strophe.Status.AUTHENTICATING) {
			onStatus("AUTHENTICATING");
		} else if (status === Strophe.Status.AUTHFAIL) {
			onStatus("AUTHFAIL");
			if(error){
				console.log("[XmppClient] error:"+error);	
			}
		} else if (status === Strophe.Status.CONNECTED) {
			if(XmppClient.xmppTimer){
				clearInterval(XmppClient.xmppTimer);
				XmppClient.xmppTimer = null;	
			}
	        XmppClient.isConnected = true;
			XmppClient.connection.send($pres());
			XmppClient.connection.addHandler(XmppClient.message, null, "message");
			sessionStorage.setItem("sid",XmppClient.connection.sid);
			onStatus("CONNECTED");
			Strophe.log = function(lvl,msg){
				if(lvl>0){
					console.log("["+lvl+"] "+msg);	
				}
			};
		} else if (status === Strophe.Status.DISCONNECTING) {
			onStatus("DISCONNECTING");
		} else if (status === Strophe.Status.DISCONNECTED) {
			onStatus("DISCONNECTED");
			console.log("[XmppClient] disconnected, error: "+error);
			if(XmppClient.xmppTimer){
				clearTimeout(XmppClient.xmppTimer);
				XmppClient.xmppTimer = null;
			}
			
			if(XmppClient.isConnected){
				sessionStorage.setItem("rid",XmppClient.connection.rid);
			}
			XmppClient.isConnected = false;
			console.log("[XmppClient] rid:"+sessionStorage.rid);
			XmppClient.connection.disconnect();
			XmppClient.connection = null;
			
			XmppClient.xmppTimer = setInterval(function(){
				XmppClient.attach();
			},10000);
			
		} else if (status === Strophe.Status.ATTACHED) {
			XmppClient.callback(Strophe.Status.CONNECTED, error);
		} else if(status === Strophe.Status.SIDEXPIRED){
			onStatus("SIDEXPIRED");
			XmppClient.connection.disconnect();
			XmppClient.connection = null;
			XmppClient.isConnected = false;
			// Alisher
			/*
			if(sessionStorage.jid && sessionStorage.pwd){
				XmppClient.connect(sessionStorage.jid,sessionStorage.pwd);
			}
			*/
			
				XmppClient.connect(login, pass);
			
			// Alisher
		}
	},
	
	attach: function() {
		XmppClient.connection = new Strophe.Connection(XmppClient.serviceUrl);
		console.log("Attaching using:"+sessionStorage.jid+" / "+sessionStorage.sid+" / "+sessionStorage.rid);
		XmppClient.connection.attach(sessionStorage.jid,sessionStorage.sid,Number(sessionStorage.rid), XmppClient.callback);
	},
	
    connect: function (id, pwd) {
		if(XmppClient.connection){
			XmppClient.connection.disconnect();
		}
		// Alisher
		login = id;
		pass = pwd;
		console.log("**Alisher; login: " + login);
		console.log("**Alisher; pass: " + pass);
		console.log("**Alisher; function connect was called");
		// Alisher	
		XmppClient.jid = id+"@"+XmppClient.domain;
		sessionStorage.setItem("pwd",pwd);
		sessionStorage.setItem("jid",XmppClient.jid);
		XmppClient.connection = new Strophe.Connection(XmppClient.serviceUrl);
		XmppClient.connection.connect(XmppClient.jid,pwd, XmppClient.callback);
    },
    
    send: function (to, msg) {
		var message = $msg({to: to+"@"+XmppClient.domain, "type": "chat"})
			.c('body').t(msg).up()
			.c('active', {xmlns: "http://jabber.org/protocol/chatstates"});
		XmppClient.connection.send(message);
    },
};