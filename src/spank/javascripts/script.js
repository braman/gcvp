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
	      $(this).text(reminders[(i % 3)]);
	    }).fadeIn();
	    if (i % 3 === 0) {
	      i = 0;
	    }
	  }, 5000);
  });
  var step1 = $("#kiosk-step-1"),
    step3 = $("#kiosk-step-3"),
    services = $("#services"),
    kiosk_services = $("#kiosk-services a"),
    print_iin = $("#kiosk-print-iin"),
    print_services = $("#kiosk-print-services"),
    backtostep1 = $("#back-to-step-1"),
    backtostep2 = $("#back-to-step-2"),
    backtostep2fromiin = $("#back-to-step-2-from-iin");


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
  