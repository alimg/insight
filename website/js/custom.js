/*!
 * Start Bootstrap - Grayscale Bootstrap Theme (http://startbootstrap.com)
 * Code licensed under the Apache License v2.0.
 * For details, see http://www.apache.org/licenses/LICENSE-2.0.
 */

// jQuery to collapse the navbar on scroll
var navbar_offset = $('.navbar-fixed-top').height();
console.log(navbar_offset);
$(window).scroll(function() {
    if ($(".navbar").offset().top > navbar_offset) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
    }
});

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
        $btn_vigilant.html('More <i class="fa fa-angle-double-right"></i>');
    } else {
        $btn_vigilant.html('Less <i class="fa fa-angle-double-left"></i>');
    }
});

$('.dropdown a').click(function() {
    var $this = $(this);
    var state = $this.hasClass('bio-open');
    $bio_on  = $this.find('.bio-on');
    $bio_off = $this.find('.bio-off');
//    $bio_on.toggle('slow');//removeClass('bio-on').addClass('bio-off');
    $bio_off.toggle('slow');//removeClass('bio-off').addClass('bio-on');
    if (state) {
        $this.find('.bio-img-on').show();
        $this.find('.bio-img-off').hide();
    } else {
        $this.find('.bio-img-off').show();
        $this.find('.bio-img-on').hide();
    }
});

$('.bio-img-off').hide();
