  var language_choose_title = $(".language-chooser-title"),
    reminders = ["Тілді таңдаңыз", "Выберите язык", "Choose your language"],
    i = 0;
  var _interval = null;
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
	  }, 3000);
  });
  var step1 = $("#kiosk-step-1"),
    step2 = $("#kiosk-step-2"),
    step3 = $("#kiosk-step-3"),
    step_iin = $("#kiosk-step-iin"),
    services = $("#services"),
    kiosk_services = $("#kiosk-services a"),
    reservation = $("#reservation"),
    print_iin = $("#kiosk-print-iin"),
    print_services = $("#kiosk-print-services"),
    backtostep1 = $("#back-to-step-1"),
    backtostep2 = $("#back-to-step-2"),
    backtostep2fromiin = $("#back-to-step-2-from-iin");

  $('#ru-lang').on('click', function() {
    step1.fadeOut(300, function() {
      step2.fadeIn(300);
    });});

  services.on('click', function() {
    step2.fadeOut(300, function() {
      step3.fadeIn(300);
    });
  });

  reservation.on('click', function() {
    step2.fadeOut(300, function() {
      step_iin.fadeIn(300);
    });
  });

  backtostep1.on('click', function(){
    step2.fadeOut(300, function(){
      step1.fadeIn(300);
    });
  });

  backtostep2fromiin.on('click', function(){
    step_iin.fadeOut(300, function(){
      step2.fadeIn(300);
    });
  });

  backtostep2.on('click', function(){
    step3.fadeOut(300, function(){
      step2.fadeIn(300);
    });
  });

  kiosk_services.on('click', function(){
    var $this = $(this);
    $this.toggleClass('active');
    $this.find('.tick-pic, figure').fadeToggle(99);

    if(kiosk_services.hasClass('active')){
      print_services.fadeIn();
    }else{
      print_services.fadeOut();
    }

  });


  /*Keyboard для ввода ИИНа*/
  $(function() {
    var $write = $('#kiosk-iin-input'),
       $backspace = $('#kiosk-backspace');

    $('#kiosk-numpad li').click(function() {
      var $this = $(this),
          length = $write.val().length,
          character = $this.html(); // If it's a lowercase letter, nothing happens to this variable

      // Add the character
      if(length > 11) {
        return false;}
      if(length > 10){
        print_iin.fadeIn();
        }
      $write.html($write.html() + character);

    });

    // Delete
    $backspace.on('click', function() {
      var html = $write.html(),
          length = $write.val().length;

      if(length < 13){
          print_iin.fadeOut();
        }
      $write.html(html.substr(0, html.length - 1));
      return false;
    });

});