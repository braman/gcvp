var $rightside = $('.rightside'), $leftside = $('.leftside'), $leftside_list = $leftside
		.children('.list'), $rightside_list = $rightside.children('.list'), 
		$ticketNumber = 101, $windowNumber = 9, $ticketId = 0;

var rightTicket = new Array();
var rightWindow = new Array();
var rightId = new Array();
var rightColor = new Array();
var leftTicket = new Array();
var leftWindow = new Array();
var leftId = new Array();
var leftColor = new Array();

$addingItem = function(ticketNumber, windowNumber, ticketId) {
	return '<li><span id = "'
			+ ticketId
			+ '" class="ticket-number" style="margin-left:-20px;">'
			+ ticketNumber
			+ '</span><span class="icon icon-arrow-right"></span><span class="window-number">'
			+ windowNumber + '</span></li>';
};

function runTicket(ticketNumber, windowNumber, myColor, ticketId) {
	if (leftTicket.length < 4) {
		for ( var i = leftTicket.length - 1; i >= 0; i--) {
			leftTicket[i + 1] = leftTicket[i];
			leftWindow[i + 1] = leftWindow[i];
			leftId[i + 1] = leftId[i];
			leftColor[i + 1] = leftColor[i];
		}
		leftTicket[0] = ticketNumber;
		leftWindow[0] = windowNumber;
		leftId[0] = ticketId;
		leftColor[0] = myColor;
	} else {
		if (rightTicket.length < 4) {
			for ( var i = rightTicket.length - 1; i >= 0; i--) {
				rightTicket[i + 1] = rightTicket[i];
				rightWindow[i + 1] = rightWindow[i];
				rightId[i + 1] = rightId[i];
				rightColor[i + 1] = rightColor[i];
			}
			rightTicket[0] = leftTicket[3];
			rightWindow[0] = leftWindow[3];
			rightId[0] = leftId[3];
			rightColor[0] = leftColor[3];
			for ( var i = leftTicket.length - 2; i >= 0; i--) {
				leftTicket[i + 1] = leftTicket[i];
				leftWindow[i + 1] = leftWindow[i];
				leftId[i + 1] = leftId[i];
				leftColor[i + 1] = leftColor[i];
			}
			leftTicket[0] = ticketNumber;
			leftWindow[0] = windowNumber;
			leftId[0] = ticketId;
			leftColor[0] = myColor;
		} else {
			rightTicket.pop();
			rightWindow.pop();
			rightId.pop();
			rightColor.pop();
			for ( var i = rightTicket.length - 1; i >= 0; i--) {
				rightTicket[i + 1] = rightTicket[i];
				rightWindow[i + 1] = rightWindow[i];
				rightId[i + 1] = rightId[i];
				rightColor[i + 1] = rightColor[i];
			}
			rightTicket[0] = leftTicket[3];
			rightWindow[0] = leftWindow[3];
			rightId[0] = leftId[3];
			rightColor[0] = leftColor[3];
			for ( var i = leftTicket.length - 2; i >= 0; i--) {
				leftTicket[i + 1] = leftTicket[i];
				leftWindow[i + 1] = leftWindow[i];
				leftId[i + 1] = leftId[i];
				leftColor[i + 1] = leftColor[i];
			}
			leftTicket[0] = ticketNumber;
			leftWindow[0] = windowNumber;
			leftId[0] = ticketId;
			leftColor[0] = myColor;
		}
	}
	draw();
}
function draw() {
	if (leftTicket.length > 0) {
		$leftside_list.empty();
		$rightside_list.empty();
		console.log(leftTicket.length);
		if (leftSide && rightSide) {
			$leftside_list.append($(
					$addingItem(leftTicket[0], leftWindow[0], leftId[0]))
					.hide().fadeIn('slow'));
			
			for ( var i = 1; i < leftTicket.length; i++) {
				$leftside_list.append($addingItem(leftTicket[i], leftWindow[i],
						leftId[i]));
			}
			for ( var i = 0; i < rightTicket.length; i++) {
				$rightside_list.append($addingItem(rightTicket[i],
						rightWindow[i], rightId[i]));
			}
			for ( var i = 0; i < leftTicket.length; i++) {
				$('#' + leftId[i]).css('color', '#' + leftColor[i]);
			}
			for ( var i = 0; i < rightTicket.length; i++) {
				$('#' + rightId[i]).css('color', '#' + rightColor[i]);
			}
		} else if (rightSide) {

			$rightside_list.append($(
					$addingItem(leftTicket[0], leftWindow[0], leftId[0]))
					.hide().fadeIn('slow'));

			for ( var i = 1; i < leftTicket.length; i++) {
				$rightside_list.append($addingItem(leftTicket[i],
						leftWindow[i], leftId[i]));
			}
			for ( var i = 0; i < leftTicket.length; i++) {
				$('#' + leftId[i]).css('color', '#' + leftColor[i]);
			}
		}
		sessionStorage.setItem('leftTicket',leftTicket);
		sessionStorage.setItem('leftWindow',leftWindow);
		sessionStorage.setItem('leftId',leftId);
		
		sessionStorage.setItem('rightTicket',rightTicket);
		sessionStorage.setItem('rightWindow',rightWindow);
		sessionStorage.setItem('rightId',rightId);
	}
}
function PlaySound() {
	$('#audio').empty();
	$('#audio')
			.html(
					"<audio autoplay='autoplay'><source src='javascripts/dash/Contact_Off.m4a'/></audio>");
	draw();
}

var _kk = {};
var _ru = {};

var announcer = null;
var dashboard = null;

var _msg = null;

var unitWindow = '';
var ticket = '';
var leftSide = true;
var rightSide = true;
var videoPath_ = null;

function _init(msg) {
	_msg = msg;
	$("head").empty();
	$("head")
			.append(
					"<meta charset='utf-8'><meta name='description' content='CQ'><meta name='viewport' content='width=device-width, initial-scale=1'><link rel='stylesheet' href='stylesheets/main.css'>");
	location.hash = "#!dashboard";
	jQuery("#connectionStatus").text("");
	document.title = 'CQ - Dashboard';

	$('#body').css('min-height', '100%');
	// setTimeout(function(){
	jQuery("#navBar").hide();
	announcer = document.getElementById("announcer");
	jQuery("#flashPanel").removeClass("hidden");
	// dashboard.setTitle(_group);
	console.log('-----------------------');
	jQuery("title").text(msg.group);
	if (msg.runningLine) {
		$('#line').text(msg.runningLine);
	}
	if (msg.tokens) {
		jQuery(msg.tokens).each(
				function(ix) {
					setTimeout(function() {
						var t = msg.tokens[ix];
						console.log('--------------1');
						console.log(t.window);
						console.log(t.ticket);
						console.log('--------------2');
						if (t.isTicketReserved == 10) {
							if (t.color) {
								runTicket(t.ticket, t.window, t.color,
										t.tokenId);
								// dashboard.push({ticket:[t.ticket,t.window],color:t.color,type:'remote'});
							} else {
								runTicket(t.ticket, t.window, '', t.tokenId);
								// dashboard.push({ticket:[t.ticket,t.window],color:'00ff00',type:'remote'});
							}
						} else {
							if (t.color) {
								console.log(t.ticket + ' ' + t.window + " "
										+ t.color + " " + t.tokenId);
								runTicket(t.ticket, t.window, t.color,
										t.tokenId);
								// dashboard.push({ticket:[t.ticket,t.window],color:t.color});
							} else {
								runTicket(t.ticket, t.window, '', t.tokenId);
								// dashboard.push({ticket:[t.ticket,t.window],color:'00ff00'});
							}

						}

					}, 200);
				});
	}
	// },5000);
	if (msg.videoPath) {
		videoPath_ = msg.videoPath;
	}
}
// videoPlay('video/ad.mp4');
function videoPlay(file) {
	$('#ad').empty();
	$('.wrapper').hide();
	$('#ad').html(
			"<video width='100%' controls='' autoplay='' name='media'><source src='"
					+ file + "' type='video/mp4'></video>");
	draw();
}
function videoStop() {
	$('#ad').empty();
	$('.wrapper').show();
	afterVideo();
}
function afterVideo() {
	if(leftTicket.length>0){
		$leftside_list.empty();
		$rightside_list.empty();
		$leftside_list.append($(
				$addingItem(leftTicket[0], leftWindow[0], leftId[0])).hide()
				.fadeIn('slow'));

		for ( var i = 1; i < leftTicket.length; i++) {
			$leftside_list.append($addingItem(leftTicket[i], leftWindow[i],
					leftId[i]));
		}
		for ( var i = 0; i < rightTicket.length; i++) {
			$rightside_list.append($addingItem(rightTicket[i], rightWindow[i],
					rightId[i]));
		}
		for ( var i = 0; i < leftTicket.length; i++) {
			$('#' + leftId[i]).css('color', '#' + leftColor[i]);
		}
		for ( var i = 0; i < rightTicket.length; i++) {
			$('#' + rightId[i]).css('color', '#' + rightColor[i]);
		}
	}	
}
// reklama
// left
// reklamaLeftPlay('video/ad.mp4');
function reklamaLeftPlay(file) {
	$('#leftReklama').empty();
	$('#leftList').hide();
	leftSide = false;
	$('#leftReklama').html(
			"<video width='100%' controls='' autoplay='' name='media'><source src='"
					+ file + "' type='video/mp4'></video>");
	jQuery("body").append(
			jQuery("<link rel='stylesheet' href='stylesheets/dashRight.css'>"));
	draw();
}
function reklamaLeftStop() {
	console.log('stop1');
	$('#leftReklama').empty();
	console.log('stop2');
	$('#leftList').show();
	console.log('stop3');
	leftSide = true;
	jQuery("body").append(
			jQuery("<link rel='stylesheet' href='stylesheets/dashLeft.css'>"));
	afterVideo();
}
// right
// reklamaRightPlay('video/ad.mp4');
function reklamaRightPlay(file) {
	$('#rightReklama').empty();
	$('#rightList').hide();
	rightSide = false;
	$('#rightReklama').html(
			"<video width='100%' controls='' autoplay='' name='media'><source src='"
					+ file + "' type='video/mp4'></video>");
}
function reklamaRightStop() {
	$('#rightReklama').empty();
	$('#rightList').show();
	rightSide = true;
	afterVideo();
}

function _onMessage(text) {
	var msg = {};
	try {
		msg = JSON.parse(text);
	} catch (e) {
		console.warn("Dashboard got non-JSON message:" + text);
		return;
	}
	console.log(msg);
	if (msg.videoAction) {
		console.log(msg.videoAction);
		if (msg.videoAction == 'reklamaLeftPlay') {
			reklamaLeftPlay(videoPath_);
		}else if (msg.videoAction == 'reklamaRightPlay') {
			reklamaRightPlay(videoPath_);
		}else if (msg.videoAction == 'videoPlay') {
			videoPlay(videoPath_);
		}else if (msg.videoAction == 'reklamaLeftStop') {
			reklamaLeftStop(videoPath_);
		}else if (msg.videoAction == 'reklamaRightStop') {
			reklamaRightStop(videoPath_);
		}else if (msg.videoAction == 'videoStop') {
			videoStop(videoPath_);
		}else if (msg.videoAction == 'runningLine') {
			$('#line').text(msg.runningLine);
		}
	}
	console.log('sulta'+msg);
	if (msg.ticket && (msg.called || msg.recalled)) {
		console.log(window + " " + ticket);
		// if(msg.window == unitWindow && msg.ticket == ticket){

		// }else{
		unitWindow = msg.window;
		ticket = msg.ticket;
		PlaySound();
		// setTimeout(function(){
		// announcer.speech([msg.ticket,msg.window],msg.lang);
		// },1000);

		if (msg.isTicketReserved) {
			runTicket(msg.ticket, msg.window, msg.color, msg.tokenId);
			// dashboard.push({ticket:[msg.ticket,msg.window],color:msg.color,type:'remote'});
		} else {
			runTicket(msg.ticket, msg.window, msg.color, msg.tokenId);
			// dashboard.push({ticket:[msg.ticket,msg.window],color:msg.color});
		}
		// }
	}
	if (msg.videoPath) {
		videoPath_ = msg.videoPath;
	}
}

function _onStatus(status) {
	console.log("_onStatus:" + ruStatuses[status]);
	if (status == "DISCONNECTED") {
		jQuery("#connectionStatus").text(ruStatuses[status]);
		jQuery("#connectionStatus").css("color", "red");
	}
}

// $(document).ready(function() {
// bot();
// });

function bot() {
	var number = 101;
	var wind = 103;
	var color = new Array('00ff00', 'ff0000', '0000ff', '000000', '00ffff');
	var index = 0;
	runTicket(number, wind++, color[index++], number++);
	runTicket(number, wind++, color[index++], number++);
	runTicket(number, wind++, color[index++], number++);
	runTicket(number, wind++, color[index--], number++);
	runTicket(number, wind++, color[index], number++);

	//	
	// setInterval(function() {
	// if (wind == 10) {
	// wind = 0;
	// }
	// if (index == 5) {
	// index = 0;
	// }
	//		
	// runTicket(number, wind, color[index], number);
	// wind++;
	// number++;
	// index++;
	// // PlaySound();
	//		
	// }, 1000);
}