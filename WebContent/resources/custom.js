$(document).ready(function() {
    //Stops the submit request
    $("#ajaxRequestForm").submit(function(e){
           e.preventDefault();
    });
    //checks for the button click event
    $("#save").click(function(e){
    	//get the form data
    	var name = prompt("Please enter query name");
    	if (name.length != 0){
            //prepare data
    	    var code = editor.getCode();
    	    console.log("code : " + code, "; name: " + name);
            dataString = {isListOperation:false, sql: code, name: name},
            //make the AJAX request, dataType is set to json
            //meaning we are expecting JSON data in response from the server
            $.ajax({
                type: "POST",
                url: "./do",
                data: dataString,
                dataType: "json",
                //if received a response from the server
                success: function( data, textStatus, jqXHR) {
                    //query was correct so we have some information to display
                     if(data.success){
                    	 console.log("success");
                    	 console.log(data.result);
                         $("#ajaxResponse").html("<br>");
                         $("#ajaxResponse").append(data.result);
                     } 
                     //display error message
                     else {
                         $("#ajaxResponse").html("<div><b>Query not saved!</b></div>");
                     }
                },
                //If there was no resonse from the server
                error: function(jqXHR, textStatus, errorThrown){
                     console.log("Something really bad happened " + textStatus);
                      $("#ajaxResponse").html(jqXHR.responseText);
                },
                //capture the request before it was sent to server
                beforeSend: function(jqXHR, settings){
                    //disable the button until we get the response
                    $('#save').attr("disabled", true);
                    Counter.resetStopwatch();
                	Counter.Timer.toggle();
                	show();
                },
                //this is called after the response or error functions are finsihed
                //so that we can take some action
                complete: function(jqXHR, textStatus){
                    //enable the button 
                    $('#save').attr("disabled", false);
                    Counter.Timer.toggle();
                    setTimeout(
                       function(){
                          location.reload();
                       }, 3000 );		
                }
            }); 
         }
    });
    
    //checks for the button click event
    $("#inquiry").click(function(e){
            //get the form data 
    	    var code = editor.getCode();
    	    console.log("code : " + code);
            //prepare data
            dataString = {isListOperation:true, sql: code},
            //make the AJAX request, dataType is set to json
            //meaning we are expecting JSON data in response from the server
            $.ajax({
                type: "POST",
                url: "./do",
                data: dataString,
                dataType: "json",
                //if received a response from the server
                success: function( data, textStatus, jqXHR) {
                    //query was correct so we have some information to display
                     if(data.success){
                    	 console.log("success");
                    	 console.log(data.result);
                         $("#ajaxResponse").html(data.result);
                     } 
                     //display error message
                     else {
                         $("#ajaxResponse").html("<div><b>Invalid request. Query result is empty!</b></div>");
                     }
                },
                //If there was no resonse from the server
                error: function(jqXHR, textStatus, errorThrown){
                     console.log("Something really bad happened " + textStatus);
                      $("#ajaxResponse").html(jqXHR.responseText);
                },
                //capture the request before it was sent to server
                beforeSend: function(jqXHR, settings){
                    //disable the button until we get the response
                    $('#inquiry').attr("disabled", true);
                    Counter.resetStopwatch();
                	Counter.Timer.toggle();
                	show();
                },
                //this is called after the response or error functions are finsihed
                //so that we can take some action
                complete: function(jqXHR, textStatus){
                    //enable the button 
                    $('#inquiry').attr("disabled", false);
                    Counter.Timer.toggle();
                }
            });        
    });
}); 

/*hide timer*/
function hide(){
	$(stopwatch).hide();
}

/*show timer*/
function show(){
	$(stopwatch).show();
}

/*Slide menu*/
jQuery(function($) {
	  var $bodyEl = $('body'),
	      $sidedrawerEl = $('#sidedrawer');

	  function showSidedrawer() {
	    // show overlay
	    var options = {
	      onclose: function() {
	        $sidedrawerEl
	          .removeClass('active')
	          .appendTo(document.body);
	      }
	    };

	    var $overlayEl = $(mui.overlay('on', options));

	    // show element
	    $sidedrawerEl.appendTo($overlayEl);
	    setTimeout(function() {
	      $sidedrawerEl.addClass('active');
	    }, 20);
	  }

	  function hideSidedrawer() {
	    $bodyEl.toggleClass('hide-sidedrawer');
	  }

	  $('.js-show-sidedrawer').on('click', showSidedrawer);
	  $('.js-hide-sidedrawer').on('click', hideSidedrawer);
	});

/*submit on CTRL + ENTER button 
<script type="text/javascript">
	var keys = {
	    shift: false,
	    ctrl: false
	};
	$(document).keydown(function(event) {
	// save status of the button 'pressed' == 'true'
	    if (event.keyCode == 17) {
	        keys["ctrl"] = true;
	    } else if (event.keyCode == 13) {
	        keys["enter"] = true;
	    }
	    if (keys["ctrl"] && keys["enter"]) {
	    	$("#request").submit(); // or do anything else
	    }
	});

	$(document).keyup(function(event) {
	    // reset status of the button 'released' == 'false'
	    if (event.keyCode == 17) {
	        keys["ctrl"] = false;
	    } else if (event.keyCode == 13) {
	        keys["enter"] = false;
	    }
	});	
</script>*/