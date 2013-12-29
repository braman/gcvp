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

var timeout = 10000;
var pingInterval;
var checkInterval;

var XmppClient = {
	connection: null,
	serviceUrl: "/http-bind/",
	domain: "cq.b2e.kz",
	
	jid: null,
	sid: null,
	rid: "0",
	
    isConnected: false,

    log: function (msg) {
        $('#log').append("<p>" + msg + "</p>");
    },
    
    callback: function (status, error) {
		XmppClient.saveState();
		if (status === Strophe.Status.ERROR) {
			onStatus("ERROR", error);
		} else if (status === Strophe.Status.CONNECTING) {
			onStatus("CONNECTING");
		} else if (status === Strophe.Status.CONNFAIL) {
			onStatus("CONNFAIL");
		} else if (status === Strophe.Status.AUTHENTICATING) {
			onStatus("AUTHENTICATING");
		} else if (status === Strophe.Status.AUTHFAIL) {
			onStatus("AUTHFAIL");
		} else if (status === Strophe.Status.CONNECTED) {
			XmppClient.isConnected = true;
			XmppClient.connection.send($pres());
			XmppClient.connection.addHandler(XmppClient.message, null, "message");
			XmppClient.startPinger();
            XmppClient.startChecker();
            onStatus("CONNECTED");
		} else if (status === Strophe.Status.DISCONNECTING) {
			onStatus("DISCONNECTING");
		} else if (status === Strophe.Status.DISCONNECTED) {
			XmppClient.clearState();
			XmppClient.connection = null;
			XmppClient.connect(null, null);
			onStatus("DISCONNECTED");
		} else if (status === Strophe.Status.ATTACHED) {
			XmppClient.connection.send($pres());
			if(XmppClient.connection.handlers.length < 1){
				XmppClient.connection.addHandler(XmppClient.message, null, "message");
			}
			onStatus("ATTACHED");
		}
	},
    
    connect: function (id, password) {
    	console.log("connecting...");
    	if(id){
			sessionStorage.setItem("xmpp-jid", id);
		}
		if(password){
			sessionStorage.setItem("xmpp-pwd", password);
		}
		if(XmppClient.connection){
			XmppClient.connection.disconnect();
		}
		XmppClient.connection = new Strophe.Connection(XmppClient.serviceUrl);
		XmppClient.connection.connect(sessionStorage.getItem("xmpp-jid")+"@"+XmppClient.domain, sessionStorage.getItem("xmpp-pwd"), XmppClient.callback);
    },
    
    reconnect: function () {
    	console.log("reconnecting...");
		if(!XmppClient.connection){
			XmppClient.connection = new Strophe.Connection(XmppClient.serviceUrl);
			if(sessionStorage.getItem("xmpp-jid") && sessionStorage.getItem("xmpp-sid") && sessionStorage.getItem("xmpp-rid")){
				XmppClient.connection.attach(sessionStorage.getItem("xmpp-jid")+"@"+XmppClient.domain, sessionStorage.getItem("xmpp-sid"), parseInt(sessionStorage.getItem("xmpp-rid"))+1, XmppClient.callback)
			}
		}
		else if(XmppClient.connection.sid && XmppClient.connection._requests && XmppClient.connection._requests.length > 0){
			XmppClient.connection.attach(sessionStorage.getItem("xmpp-jid")+"@"+XmppClient.domain, XmppClient.connection.sid, parseInt(XmppClient.connection._requests[0].rid)+1, XmppClient.callback);
		}
		else {
			XmppClient.connect(null, null);		
		}
    },
    
    send: function (to, msg) {
		var message = $msg({to: to+"@"+XmppClient.domain, "type": "chat"})
			.c('body').t(msg).up()
			.c('active', {xmlns: "http://jabber.org/protocol/chatstates"});
	
		XmppClient.connection.send(message);
    },
    
    message: function (message) {
		var body = $(message).find("html > body");
		if (body.length === 0) {
			body = $(message).find('body');
			if (body.length > 0) {
				body = body.text()
			}
			else {
				body = null;
			}
		}
		else {
			body = body.contents();
	
			var span = $("<span></span>");
			body.each(function () {
				if (document.importNode) {
					$(document.importNode(this, true)).appendTo(span);
				}
				else {
					// IE workaround
					span.append(this.xml);
				}
			});
	
			body = span;
		}
	
		if (body) {
			console.log("onMessage:"+body);
			onMessage(body);
		}
	
		return true;
    },
    
    startPinger: function () {
		pingInterval = setInterval( function() { 
			if(XmppClient.connection){
				console.log("ping");
				var ping = $iq({
					to: Strophe.getDomainFromJid(XmppClient.connection.jid),
					type: "get",
					id: "ping"}).c("ping", {xmlns: "urn:xmpp:ping"});
	
				XmppClient.connection.send(ping);
			}
		}, timeout);
    },
    
    startChecker: function () {
		checkInterval = setInterval( function() { 
			console.log("connected: " + XmppClient.connection.connected);
			if(!XmppClient.connection || !XmppClient.connection.connected){
				XmppClient.reconnect();
			}
		}, timeout);
	},
    
    saveState: function () {
		sessionStorage.setItem("xmpp-sid", XmppClient.connection.sid);
		sessionStorage.setItem("xmpp-rid", XmppClient.connection.rid);
	},
    
    clearState: function () {
    	sessionStorage.setItem("xmpp-sid", null);
    	sessionStorage.setItem("xmpp-rid", null);
    }
};