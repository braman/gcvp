
 // Stuff to do as soon as the DOM is ready;
 var $call = $("#call"),
  $callpostponed = $("#callpostponed"),
  $forward = $("#forward"),
  $postpone = $("#postpone"),
  $putinqueue = $("#putinqueue"),
  $start = $("#start"),
  $stop = $("#stop"),
  $anketa = $("#anketa"),
  $start_counter = $("#start-counter"),
  $callpostponed_confirm = ("#callpostponed_confirm"),

  $operator_step_1 = $("#operator-step-1"),
  $operator_step_2 = $("#operator-step-2"),
  $operator_step_3 = $("#operator-step-3"),
  $operator_step_4 = $("#operator-step-4"),

  $operator_back_to_step_1 = $("#operator-back-to-step-1"),

  $reveal_forward_links = $("#reveal-forward-links"),
  fadeOutTime = 180, //2s;

  operator_steps = {
   back_to_step_1_from_step_2: function() {
	   step = '';
           //If!
           if ($start_counter.is(":visible")) {
            $operator_step_2.add($start_counter).add($stop).fadeOut(fadeOutTime, function() {
             $operator_step_1.add($start).fadeIn(fadeOutTime);
            })
           } else {
            $operator_step_2.fadeOut(fadeOutTime, function() {
             $operator_step_1.fadeIn(fadeOutTime);
            });
           };
           //If! Ends..
          }
  };
 
 function showPostponedModal(){
	 if(firstTime==0){
			firstTime++;
 		jQuery("body").append(jQuery("<script src='javascripts/jquery.reveal.js'></script"));
     }
	 $('#callpostponed_modal').reveal();
 }
 
 function callBtnFadeIn()
{
	step = 'called';
	console.log('callFadeIn');
	$operator_step_1.fadeOut(fadeOutTime, function() {
	   $operator_step_2.fadeIn(fadeOutTime);
	 })
}
