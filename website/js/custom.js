/*!
 * Start Bootstrap - Grayscale Bootstrap Theme (http://startbootstrap.com)
 * Code licensed under the Apache License v2.0.
 * For details, see http://www.apache.org/licenses/LICENSE-2.0.
 */

// jQuery to collapse the navbar on scroll
var navbar_offset = $('.navbar-fixed-top').height();
var logo_size = $('#logo-sml').parent().height();
var $logo_big = $('#logo-big');
var $logo_sml = $('#logo-sml');
$('#logo-sml').hide();
$('.navbar-brand').height(logo_size);
$(window).scroll(function() {
    if ($(".navbar").offset().top > navbar_offset) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
        $logo_big.hide('slow');
        $logo_sml.show('slow');
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
        $logo_big.show('slow');
        $logo_sml.hide('slow');
    }
}).scroll();

// jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
        if ($anchor.attr('href') == "#vigilant") {
        $('html, body').stop().animate({
            scrollTop: 0
        }, 1000, 'easeInOutExpo');
        return;
        }
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1500, 'easeInOutExpo');
        event.preventDefault();
    });
});

// Closes the Responsive Menu on Menu Item Click
$('.navbar-collapse ul li a').click(function() {
    $('.navbar-toggle:visible').click();
});

var $btn_vigilant = $('#btn-vigilant');
$btn_vigilant.click(function() {
    var state = $($btn_vigilant.attr('data-target')).hasClass('in');
    if (state) {
        $btn_vigilant.html('More About Us <i class="fa fa-angle-double-right"></i>');
    } else {
        $btn_vigilant.html('Less <i class="fa fa-angle-double-left"></i>');
    }
});

$('.dropdown a').click(function(e) {
    var $this = $(this);
    var $target = $($this.attr('data-target'));
    var state = $target.hasClass('in');
    if (state) {
        $target.removeClass('in').hide(300);
        $this.find('.bio-img-off').show();
        $this.find('.bio-img-on').hide();
    } else {
        $target.addClass('in').show(300);
        $this.find('.bio-img-on').show();
        $this.find('.bio-img-off').hide();
    }
    e.preventDefault();
});

$('.bio-long').hide();
$('.bio-img-on').hide();
