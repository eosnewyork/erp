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
});
