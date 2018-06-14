$(function() {
    "use strict";

    $('#menu-button, #menu-close-button').on('click touchend', function(e) {
        e.preventDefault();

        $('body').toggleClass('pushed-left');
        $('#menu-button').toggleClass('open');

    });



    $('.with-tile-below .absolute-bottom').on('click', function(e) {
        e.preventDefault();
        $(this).parent('.image-overlay').stop().addClass('pushed-right');
		});



	$('.with-tile-below').on('mouseleave', function(e) {
        e.preventDefault();
        $(this).children('.image-overlay').stop().removeClass('pushed-right');
		});



    $('.panel-heading a').on('click', function() {
        $('.panel-heading').removeClass('active');
        if (!$(this).closest('.panel').find('.panel-collapse').hasClass('in')) {
            $(this).parents('.panel-heading').addClass('active');
        }
    });


    $('body').not('body.touch').on('click touchend', function(e) {
        var $body = $(this);
        var $target = $(e.target);
        if (($body.hasClass('pushed-left-alt') || $body.hasClass('pushed-left')) && $target.closest('#main-nav').length === 0 && $target.closest('#menu-button').length === 0) {
            e.preventDefault();
            $body.removeClass('pushed-left-alt').removeClass('pushed-left');
        }
    });

    $('#main-nav a').not('.sub-nav a, a.sub-nav-toggle').on('click', function() {
        $('body').removeClass('pushed-left-alt').removeClass('pushed-left');
        $('.sub-nav').stop().slideUp(420, function() {
            $(this).addClass('hidden');
        });
    });


    $('.sub-nav-toggle').on('click touchend', function(e) {
        e.preventDefault();
        var $subNav = $(this).next('.sub-nav');
        if ($subNav.hasClass('hidden')) {
            $subNav.hide().removeClass('hidden').stop().slideDown(420);
        } else {
            $subNav.stop().slideUp(420, function() {
                $(this).addClass('hidden');
            });
        }
    });

	$('#main-nav li').on('mouseleave', function(e) {
            e.preventDefault();
            var $subNav2 = $(this).children('.sub-nav');
            $subNav2.stop().slideUp(420, function() {
                    $(this).addClass('hidden');
                });
        });




    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        $('body').addClass('touch');
    }




    // Set the date we're counting down to
  //  var countDownDate = new Date("Sep 5, 2018 09:00:00").getTime();

    // Update the count down every 1 second
  /*  var x = setInterval(function() {

        // Get todays date and time
        var now = new Date().getTime();

        // Find the distance between now an the count down date
        var distance = countDownDate - now;

        // Time calculations for days, hours, minutes and seconds
        var days = Math.floor(distance / (1000 * 60 * 60 * 24));
        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);

        // Output the result in an element with id="demo"
        document.getElementById("demo").innerHTML = "<div>" + days + "<span>days</span>" + "</div>" + "<b>:</b>" +"<div>" +hours + "<span>hrs</span>" + "</div>" + "<b>:</b>" + "<div>" + minutes + "<span>min</span>" + "</div>" + "<b>:</b>" + "<div>" + seconds + "<span>sec</span>" + "</div>";

        // If the count down is over, write some text
        if (distance < 0) {
            clearInterval(x);
            document.getElementById("demo").innerHTML = "EXPIRED";
        }
    }, 1000);*/

});
