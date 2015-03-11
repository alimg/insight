/*!
 * Start Bootstrap - Grayscale Bootstrap Theme (http://startbootstrap.com)
 * Code licensed under the Apache License v2.0.
 * For details, see http://www.apache.org/licenses/LICENSE-2.0.
 */

// jQuery to collapse the navbar on scroll
var navbar_offset = $('.navbar-fixed-top').height();
$('#logo-sml').hide();
$(window).scroll(function() {
    if ($(".navbar").offset().top > navbar_offset) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
        $("#logo-big").hide('slow');
        $("#logo-sml").show('slow');
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
        $("#logo-big").show('slow');
        $("#logo-sml").hide('slow');
    }
}).scroll();

// jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
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

$('.dropdown a').click(function() {
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
});

$('.bio-long').hide();
$('.bio-img-on').hide();
